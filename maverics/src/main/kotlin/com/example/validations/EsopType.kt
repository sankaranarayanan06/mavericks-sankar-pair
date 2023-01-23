package com.example.validations


fun isValidESOPType(esopType: String): Boolean{
    return (esopType == "PERFORMANCE" || esopType == "NON_PERFORMANCE")
}
