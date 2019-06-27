package com.example.driver

import com.mysql.cj.jdbc.MysqlDataSource
import driver.Models
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode


class MysqlDatasourceProvider : DatasourceProvider {
    override val data: KotlinEntityDataStore<Persistable> by lazy {
        val dataSource = MysqlDataSource()
        dataSource.setURL("jdbc:mysql://localhost:3306/test")
        dataSource.user = "test"
        dataSource.password = "test"
        val configuration = KotlinConfiguration(
            model = Models.DEFAULT,
            dataSource = dataSource,
            statementCacheSize = 0,
            useDefaultLogging = true
        )
        SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)
        KotlinEntityDataStore<Persistable>(configuration)
    }
}
