#!/usr/bin/env python3
"""
2D collision checker for circles and rectangles (rotation about top-left corner).
Coordinate system: x increases to the right, y increases downward (screen-style);
positive angle is counterclockwise in this plane.
"""

from __future__ import annotations

import argparse
import math
import re
import sys
from dataclasses import dataclass
from typing import Iterable, List, Sequence, Tuple, Union

import matplotlib.pyplot as plt
import numpy as np
from matplotlib.patches import Circle as MplCircle
from matplotlib.patches import Polygon as MplPolygon

Vec2 = Tuple[float, float]


def _dot(a: Vec2, b: Vec2) -> float:
    return a[0] * b[0] + a[1] * b[1]


def _sub(a: Vec2, b: Vec2) -> Vec2:
    return (a[0] - b[0], a[1] - b[1])


def _add(a: Vec2, b: Vec2) -> Vec2:
    return (a[0] + b[0], a[1] + b[1])


def _len_sq(v: Vec2) -> float:
    return v[0] * v[0] + v[1] * v[1]


def _perp(v: Vec2) -> Vec2:
    return (-v[1], v[0])


def _normalize(v: Vec2) -> Vec2:
    l = math.hypot(v[0], v[1])
    if l == 0:
        return (0.0, 0.0)
    return (v[0] / l, v[1] / l)


@dataclass
class CircleObj:
    name: str
    cx: float
    cy: float
    r: float


@dataclass
class RectObj:
    name: str
    left: float
    top: float
    width: float
    height: float
    angle_deg: float

    def angle_rad(self) -> float:
        return math.radians(self.angle_deg)

    def axes(self) -> Tuple[Vec2, Vec2]:
        """Unit vectors along width (from pivot) and height (from pivot), y-down CCW."""
        t = self.angle_rad()
        u = (math.cos(t), math.sin(t))
        v = (-math.sin(t), math.cos(t))
        return u, v

    def corners(self) -> List[Vec2]:
        u, v = self.axes()
        p0 = (self.left, self.top)
        p1 = _add(p0, (u[0] * self.width, u[1] * self.width))
        p3 = _add(p0, (v[0] * self.height, v[1] * self.height))
        p2 = _add(p1, (v[0] * self.height, v[1] * self.height))
        return [p0, p1, p2, p3]


Object = Union[CircleObj, RectObj]


# --- Required collision primitives -------------------------------------------------


def collide_circles(a: CircleObj, b: CircleObj) -> bool:
    """True if closed disks intersect (includes containment and touching)."""
    dx = a.cx - b.cx
    dy = a.cy - b.cy
    return dx * dx + dy * dy <= (a.r + b.r) ** 2


def collide_circle_rectangle_aabb(c: CircleObj, r: RectObj) -> bool:
    """Circle vs axis-aligned rectangle (angle == 0). r: top-left, y increases down."""
    # Closest point in the half-open box [left, left+w] x [top, top+h] would miss
    # touching edges; use inclusive bounds for shared-boundary = collision.
    x_min, x_max = r.left, r.left + r.width
    y_min, y_max = r.top, r.top + r.height
    qx = min(max(c.cx, x_min), x_max)
    qy = min(max(c.cy, y_min), y_max)
    ddx = c.cx - qx
    ddy = c.cy - qy
    return ddx * ddx + ddy * ddy <= c.r * c.r


def collide_circle_rectangle_obb(c: CircleObj, r: RectObj) -> bool:
    """Circle vs rotated rectangle: transform to local (s,t) with 0<=s<=w, 0<=t<=h."""
    u, v = r.axes()
    p0 = (r.left, r.top)
    d = _sub((c.cx, c.cy), p0)
    s = _dot(d, u)
    t = _dot(d, v)
    sc = min(max(s, 0.0), r.width)
    tc = min(max(t, 0.0), r.height)
    ds = s - sc
    dt = t - tc
    return ds * ds + dt * dt <= c.r * c.r


def collide_circle_rectangle(c: CircleObj, r: RectObj) -> bool:
    """Dispatch: AABB fast path vs OBB."""
    if math.isclose(r.angle_deg, 0.0, abs_tol=1e-12):
        return collide_circle_rectangle_aabb(c, r)
    return collide_circle_rectangle_obb(c, r)


def _project(poly: Sequence[Vec2], axis: Vec2) -> Tuple[float, float]:
    ax = axis[0]
    ay = axis[1]
    first = poly[0][0] * ax + poly[0][1] * ay
    mn = mx = first
    for i in range(1, len(poly)):
        p = poly[i][0] * ax + poly[i][1] * ay
        if p < mn:
            mn = p
        elif p > mx:
            mx = p
    return mn, mx


def _intervals_overlap(a0: float, a1: float, b0: float, b1: float) -> bool:
    return not (a1 < b0 or b1 < a0)


def collide_rectangles_sat(a: RectObj, b: RectObj) -> bool:
    """OBB vs OBB via SAT (4 candidate axes: 2 edge normals per rectangle)."""
    ca = a.corners()
    cb = b.corners()
    axes: List[Vec2] = []
    for poly in (ca, cb):
        n = len(poly)
        for i in range(n):
            e = _sub(poly[(i + 1) % n], poly[i])
            axes.append(_normalize(_perp(e)))
    for axis in axes:
        if axis[0] == 0 and axis[1] == 0:
            continue
        amin, amax = _project(ca, axis)
        bmin, bmax = _project(cb, axis)
        if not _intervals_overlap(amin, amax, bmin, bmax):
            return False
    return True


def collide_rectangles_aabb(a: RectObj, b: RectObj) -> bool:
    """Separating axis test reduces to interval overlap on x and y when angles are 0."""
    ax0, ax1 = a.left, a.left + a.width
    ay0, ay1 = a.top, a.top + a.height
    bx0, bx1 = b.left, b.left + b.width
    by0, by1 = b.top, b.top + b.height
    x_overlap = _intervals_overlap(ax0, ax1, bx0, bx1)
    y_overlap = _intervals_overlap(ay0, ay1, by0, by1)
    return x_overlap and y_overlap


def collide_rectangles(a: RectObj, b: RectObj) -> bool:
    if math.isclose(a.angle_deg, 0.0, abs_tol=1e-12) and math.isclose(
        b.angle_deg, 0.0, abs_tol=1e-12
    ):
        return collide_rectangles_aabb(a, b)
    return collide_rectangles_sat(a, b)


def objects_collide(x: Object, y: Object) -> bool:
    if isinstance(x, CircleObj) and isinstance(y, CircleObj):
        return collide_circles(x, y)
    if isinstance(x, CircleObj) and isinstance(y, RectObj):
        return collide_circle_rectangle(x, y)
    if isinstance(x, RectObj) and isinstance(y, CircleObj):
        return collide_circle_rectangle(y, x)
    if isinstance(x, RectObj) and isinstance(y, RectObj):
        return collide_rectangles(x, y)
    raise TypeError(type(x), type(y))


# --- Parsing -----------------------------------------------------------------------


def _split_line(line: str) -> List[str]:
    return [p for p in re.split(r"[\s\t]+", line.strip()) if p]


def parse_objects(path: str) -> List[Object]:
    out: List[Object] = []
    with open(path, encoding="utf-8") as f:
        for raw in f:
            line = raw.strip()
            if not line or line.startswith("!"):
                continue
            parts = _split_line(line)
            if len(parts) < 2:
                continue
            kind = parts[0].lower()
            if kind == "circle":
                if len(parts) != 5:
                    raise ValueError(f"Bad circle line: {raw!r}")
                name, cx, cy, rad = parts[1], float(parts[2]), float(parts[3]), float(parts[4])
                out.append(CircleObj(name, cx, cy, rad))
            elif kind == "rectangle":
                if len(parts) != 7:
                    raise ValueError(f"Bad rectangle line: {raw!r}")
                name = parts[1]
                x, y, w, h, ang = map(float, parts[2:7])
                out.append(RectObj(name, x, y, w, h, ang))
            else:
                raise ValueError(f"Unknown object type: {parts[0]!r}")
    return out


# --- Visualization & main ----------------------------------------------------------


def _draw(objects: Sequence[Object], colliding_names: set, save_path: str | None) -> None:
    fig, ax = plt.subplots(figsize=(12, 10))
    for obj in objects:
        hit = obj.name in colliding_names
        color = "#d62728" if hit else "#1f77b4"
        edge = "#7f0f14" if hit else "#0d3d6b"
        if isinstance(obj, CircleObj):
            ax.add_patch(
                MplCircle(
                    (obj.cx, obj.cy),
                    obj.r,
                    facecolor=color,
                    edgecolor=edge,
                    linewidth=1.2,
                    alpha=0.55,
                )
            )
        else:
            poly = MplPolygon(
                obj.corners(),
                closed=True,
                facecolor=color,
                edgecolor=edge,
                linewidth=1.2,
                alpha=0.55,
            )
            ax.add_patch(poly)
    ax.set_aspect("equal", adjustable="box")
    ax.invert_yaxis()
    ax.set_xlabel("x")
    ax.set_ylabel("y (down)")
    ax.set_title("Objects (red = involved in at least one collision)")
    margin = 30
    xs: List[float] = []
    ys: List[float] = []
    for obj in objects:
        if isinstance(obj, CircleObj):
            xs.extend([obj.cx - obj.r, obj.cx + obj.r])
            ys.extend([obj.cy - obj.r, obj.cy + obj.r])
        else:
            for px, py in obj.corners():
                xs.append(px)
                ys.append(py)
    ax.set_xlim(min(xs) - margin, max(xs) + margin)
    ax.set_ylim(max(ys) + margin, min(ys) - margin)
    plt.tight_layout()
    if save_path:
        fig.savefig(save_path, dpi=120)
        plt.close(fig)
    else:
        plt.show()


def main(argv: List[str] | None = None) -> int:
    p = argparse.ArgumentParser(description="2D collision checker for circles and rectangles.")
    p.add_argument(
        "file",
        nargs="?",
        default="objects.txt",
        help="Object definition file (default: objects.txt in cwd)",
    )
    p.add_argument(
        "--save-figure",
        metavar="PATH",
        default=None,
        help="Save the scene to an image instead of opening a window (e.g. for CI).",
    )
    args = p.parse_args(argv)

    objects = parse_objects(args.file)
    n = len(objects)
    pairs: List[Tuple[str, str]] = []
    for i in range(n):
        for j in range(i + 1, n):
            if objects_collide(objects[i], objects[j]):
                a, b = objects[i].name, objects[j].name
                pairs.append((a, b) if a < b else (b, a))
    pairs.sort()
    colliding: set = set()
    for a, b in pairs:
        colliding.add(a)
        colliding.add(b)

    _draw(objects, colliding, args.save_figure)

    for a, b in pairs:
        print(f"{a} {b}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
