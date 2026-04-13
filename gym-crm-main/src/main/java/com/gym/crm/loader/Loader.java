package com.gym.crm.loader;

import com.gym.crm.storage.StorageInitializer;

public interface Loader {
    // the lower this number is the earliest the loader will run basically.
    int getOrder();
    void load(StorageInitializer.SeedData seedData);

}
