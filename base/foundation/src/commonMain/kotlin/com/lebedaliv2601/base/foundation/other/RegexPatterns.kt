package com.lebedaliv2601.base.foundation.other

const val HTML_LINKS_PATTERN = "<a[^>]*href=[\"']([^\"']*)[\"'][^>]*>([^<]*)</a>"

const val LINKS_PATTERN = """(https?://[^\s<]+)"""

const val MARKDOWN_LINK_PATTERN = "\\[(.*?)\\]\\((.*?)\\)"

const val UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"

const val EMAIL_REGEX_PATTERN = "[a-zA-Z0-9][a-zA-Z0-9\\-\\.\\_]+@[a-zA-Z0-9]{2,63}\\.[a-zA-Z]{2,9}"

const val PHONE_REGEX_PATTERN = "(^([+]?7)[98][0-9]{9}\$)|(^7[98][0-9]{10}\$):RUSMS,(^7[76][0-9]{9}\$)" +
    "|(^375[0-9]{9}\$)|(^996[0-9]{9}\$)|(^374[0-9]{8}\$)|(^998[0-9]{9}\$):RU,(^994[0-9]{9}\$):AZ,(^86[0-9]{10}\$)" +
    "|(^86[0-9]{11}\$)|(^86[0-9]{12}\$)|(^852[0-9]{8}\$)|(^853[0-9]{8}\$)"