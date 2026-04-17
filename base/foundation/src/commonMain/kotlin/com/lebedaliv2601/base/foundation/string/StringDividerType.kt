package com.lebedaliv2601.base.foundation.string

enum class StringDividerType(val value: String) {
    PointWithDoubleSpace("  ${StrConsts.POINT}  "),
    PointWithSpace(" ${StrConsts.POINT} "),
    PointWithSpaceSmall(" ${StrConsts.POINT_SMALL} "),
    ArrowRightSpace(" ${StrConsts.ARROW_RIGHT} "),
    CommaSpace("${StrConsts.COMMA} "),
    SlashSpace(" ${StrConsts.SLASH} "),
    Slash(StrConsts.SLASH),
    Space(StrConsts.SPACE),
    DashSpace(" ${StrConsts.DASH} "),
    Dash(StrConsts.DASH),
    LongDashSpace(" ${StrConsts.LONG_DASH} "),
    PointsWithSpace(" ${StrConsts.POINT}${StrConsts.POINT}${StrConsts.POINT}${StrConsts.POINT} ")
}

fun formatStringsWithDividerPoints(
    strings: Array<String?>,
    divider: StringDividerType
): String = strings
    .filterNot { it.isNullOrEmpty() }
    .joinToString(divider.value)
