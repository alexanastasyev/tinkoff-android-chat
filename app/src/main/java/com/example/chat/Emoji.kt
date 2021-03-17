package com.example.chat

import java.io.Serializable

class Emoji(val unicode: Int): Serializable {

//    FACE_SMILING(0x1F600),
//    FACE_LAUGHING(0x1F602),
//    FACE_WINKING(0x1F609),
//    FACE_IN_LOVE(0x1F60D),
//    FACE_KISSING(0x1F618),
//    FACE_NEUTRAL(0x1F610),
//    FACE_WITH_SUNGLASSES(0x1F60E),
//    FACE_CRYING(0x1F622),
//    FACE_WITH_TONGUE(0x1F61B),
//    FACE_WITH_RAISED_EYEBROW(0x1F928),
//    FACE_SMIRKING(0x1F60F),
//    FACE_RELIEVED(0x1F60C),
//    FACE_COWBOY_HAT(0x1F920),
//    FACE_ASTONISHED(0x1F632),
//    FACE_WEARY(0x1F629),
//    SKULL(0x1F480),
//    SIGN_PLUS(0x2795);

    override fun toString(): String {
        return getEmojiByUnicode(this.unicode)
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }
}