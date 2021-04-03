package com.example.chat.entities

import java.io.Serializable

class Emoji(val unicode: Int): Serializable {

    companion object {
        val FACE_SMILING = Emoji(0x1F600)
        val FACE_LAUGHING = Emoji(0x1F602)
        val FACE_WINKING = Emoji(0x1F609)
        val FACE_IN_LOVE = Emoji(0x1F60D)
        val FACE_KISSING = Emoji(0x1F618)
        val FACE_NEUTRAL = Emoji(0x1F610)
        val FACE_WITH_SUNGLASSES = Emoji(0x1F60E)
        val FACE_CRYING = Emoji(0x1F622)
        val FACE_WITH_TONGUE = Emoji(0x1F61B)
        val FACE_WITH_RAISED_EYEBROW = Emoji(0x1F928)
        val FACE_SMIRKING = Emoji(0x1F60F)
        val FACE_RELIEVED = Emoji(0x1F60C)
        val FACE_COWBOY_HAT = Emoji(0x1F920)
        val FACE_ASTONISHED = Emoji(0x1F632)
        val FACE_WEARY = Emoji(0x1F629)
        val SKULL = Emoji(0x1F480)
        val SIGN_PLUS = Emoji(0x2795)
    }

    override fun toString(): String {
        return getEmojiByUnicode(this.unicode)
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}