package com.example.driver

import io.requery.Persistable
import io.requery.sql.KotlinEntityDataStore

interface DatasourceProvider {
    val data: KotlinEntityDataStore<Persistable>
}