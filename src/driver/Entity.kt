package driver

import io.requery.Entity
import io.requery.Key
import io.requery.Persistable
import io.requery.Table

@Entity
@Table(name = "tests")
interface MessageEntity : Persistable {
    @get:Key
    var id: Int
    var title: String
}