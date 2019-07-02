package driver

import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.kotlin.findAttribute
import io.requery.sql.KotlinEntityDataStore


class MessageDao(private val data: KotlinEntityDataStore<Persistable>) : MessageDriver {

    override fun findAll(): List<MessageEntity> {
        return data.withTransaction {
            select(MessageEntity::class).get()
        }.toList()
    }

    override fun find(id: Int): MessageEntity? {
        return data.withTransaction {
            val cond = MessageEntity::id eq id
            val result = select(MessageEntity::class) where (cond) limit 1
            result.get()
        }.firstOrNull()
    }

    override fun find(title: String): List<MessageEntity> {
        return data.withTransaction {
            val cond = MessageEntity::title eq title
            val result = select(MessageEntity::class) where (cond)
            result.get()
        }.toList()
    }

    override fun upsert(message: MessageEntity) {
        data.withTransaction {
            println("upsert")
            var messageEntity = MessageEntityEntity()
            messageEntity.id = message.id
            messageEntity.title = message.title
            upsert(messageEntity)
        }
    }

    override fun update(message: MessageEntity): Int {
        return data.withTransaction {
            val cond = MessageEntity::id eq message.id
            update(MessageEntity::class)
                .set(findAttribute(MessageEntity::title), message.title)
                .where(cond)
        }.get().value()
    }

    override fun delete(id: Int): Int {
        return data.invoke {
            val cond = MessageEntity::id eq id
            delete(MessageEntity::class) where (cond)
        }.get().value()
    }
}