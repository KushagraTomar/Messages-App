package com.kush.messages_app

class Message {
    var message: String? = null
    var senderId: String? = null
    constructor() { }
    constructor(message: String?, senderId: String?) {
        this.message = message
        this.senderId = senderId
    }
}