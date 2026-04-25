package com.sentinel.app.api

data class CveResponse(
    val vulnerabilities: List<CveItem>
)

data class CveItem(
    val cve: CveDetail
)

data class CveDetail(
    val id: String,
    val descriptions: List<CveDescription>,
    val published: String,
    val metrics: CveMetrics?
)

data class CveDescription(
    val lang: String,
    val value: String
)

data class CveMetrics(
    val cvssMetricV2: List<CvssMetricV2>?
)

data class CvssMetricV2(
    val baseSeverity: String?
)