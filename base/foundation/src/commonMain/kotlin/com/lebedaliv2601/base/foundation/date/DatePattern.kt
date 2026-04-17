package com.lebedaliv2601.base.foundation.date

/**
 * Базовый интерфейс паттерна форматирования даты/времени.
 */
interface DatePattern {
    val value: String
}

enum class DefaultDatePattern(override val value: String) : DatePattern {

    /**
     * Цифровые ->
     *
     * Пример: 2024-03-15
     */
    YYYY_MM_DD("yyyy-MM-dd"),

    /** Пример: 2024.03.15 */
    YYYY_MM_DD_DOT("yyyy.MM.dd"),

    /** Пример: 2024-03-15 14:30 */
    YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),

    /** Пример: 2024-03-15 14:30:45 */
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),

    /** Пример: 2024-03-15T14:30:45 */
    YYYY_MM_DD_T_HH_MM_SS("yyyy-MM-dd'T'HH:mm:ss"),

    /** Пример: 2024-03-15T14:30:45.123 */
    YYYY_MM_DD_T_HH_MM_SS_SSS("yyyy-MM-dd'T'HH:mm:ss.SSS"),

    /** Пример: 2024-03-15T14:30:45.123456 */
    YYYY_MM_DD_T_HH_MM_SS_SSSSSS("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),

    /** Пример: 15.03.2024 */
    DD_MM_YYYY("dd.MM.yyyy"),

    /** Пример: 15.03.2024 14:30 */
    DD_MM_YYYY_HH_MM("dd.MM.yyyy HH:mm"),

    /** Пример: 14:30 15.03.2024 */
    HH_MM_DD_MM_YYYY("HH:mm dd.MM.yyyy"),

    /** Пример: 15.03.2024 (14:30) */
    DD_MM_YYYY_HH_MM_BRACKETS("dd.MM.yyyy (HH:mm)"),

    /** Пример: 15.03.24 */
    DD_MM_YY("dd.MM.yy"),

    /** Пример: 15.03.24, 14:30 */
    DD_MM_YY_HH_MM("dd.MM.yy, HH:mm"),

    /** Пример: 15.03 */
    DD_MM("dd.MM"),

    /** Пример: 15-03-2024 */
    DD_MM_YYYY_DASH("dd-MM-yyyy"),

    /** Пример: 15.03.2024-14_30_45 */
    DD_MM_YYYY_FILE("dd.MM.yyyy-HH_mm_ss"),

    /** Пример: 15 */
    D("d"),

    /** Пример: 14 (час без ведущего нуля) */
    H("H"),

    /** Пример: 14:30 */
    HH_MM("HH:mm"),

    /**
     * Локализуемые ->
     *
     * Пример: 15 мар. 2024
     */
    D_MMM_YYYY("d MMM yyyy"),

    /** Пример: 15 мар. */
    D_MMM("d MMM"),

    /** Пример: пятница, 15 мар. */
    EEEE_D_MMM("EEEE, d MMM"),

    /** Пример: 14:30 15 мар. */
    HH_MM_D_MMM("HH:mm d MMM"),

    /** Пример: 15 мар. 2024, 14:30 */
    D_MMM_YYYY_HH_MM("d MMM yyyy, HH:mm"),

    /** Пример: 15 мар., 14:30 */
    D_MMM_HH_MM("d MMM, HH:mm"),

    /** Пример: 15 марта, 14:30 */
    D_MMMM_HH_MM("d MMMM, HH:mm"),

    /** Пример: 15 марта 2024, 14:30 */
    D_MMMM_YYYY_HH_MM("d MMMM yyyy, HH:mm"),

    /** Пример: 15 марта */
    D_MMMM("d MMMM"),

    /** Пример: 15 марта 2024 */
    DD_MMMM_YYYY("dd MMMM yyyy"),

    /** Пример: 15 марта 2024 */
    D_MMMM_YYYY("d MMMM yyyy"),

    /** Пример: Март (название месяца) */
    LLLL("LLLL"),

    /** Пример: Март 2024 */
    LLLL_YYYY("LLLL yyyy"),
}