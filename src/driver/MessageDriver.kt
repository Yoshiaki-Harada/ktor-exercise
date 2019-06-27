package driver


interface MessageDriver {
    fun findAll(): List<MessageEntity>
    fun find(id: Int): MessageEntity?
    fun find(title: String): List<MessageEntity>
    fun upsert(message: MessageEntity)
    fun update(message: MessageEntity) :Int
    fun delete(id: Int): Int
}