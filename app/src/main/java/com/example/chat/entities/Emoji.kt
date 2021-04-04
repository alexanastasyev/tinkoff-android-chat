package com.example.chat.entities

import de.ppi.oss.kzulip.api.common.EmojiEnum
import java.io.Serializable

class Emoji(val unicode: Int): Serializable {

    companion object {
        val SIGN_PLUS = Emoji(0x2795)
        fun getEmojiNameByUnicode(unicode: Int): String {
            for (emoji in EmojiEnum.values()) {
                if (emoji.unicodeCodePoint == unicode) {
                    return emoji.nameInZulip
                }
            }
            return ""
        }
    }

    override fun toString(): String {
        return getEmojiByUnicode(this.unicode)
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}