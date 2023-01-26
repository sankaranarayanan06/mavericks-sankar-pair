package com.example.services

import com.example.constants.*
import com.example.model.VestingData
import java.time.LocalDateTime

fun addESOPVestings(username: String, quantity: Long, esopType: String){

    var previousQuantity: Long = 0
    var totalPercentage: Long = 0

    val systemTime = LocalDateTime.now()

    for(i in 0 until vestingPercentages.size)
    {
        totalPercentage += vestingPercentages[i]

        val expectedQuantity = (quantity * totalPercentage) / 100

        val currentDayQuantity = expectedQuantity - previousQuantity

        previousQuantity += currentDayQuantity

        vestings[username]!!.add(VestingData(quantity=currentDayQuantity, time = systemTime.plusSeconds(vestingTimings[i]), esopType = esopType))
    }

}

fun performESOPVestings(username: String)
{
    val systemTime = LocalDateTime.now()
    while(vestings[username]!!.size > 0)
    {
        val vestingEntry = vestings[username]!![0]

        if(vestingEntry.time.isBefore(systemTime))
        {
            if(vestingEntry.esopType == "PERFORMANCE"){
                inventoryData[username]!![0].free += vestingEntry.quantity
            }
            else
            {
                inventoryData[username]!![1].free += vestingEntry.quantity
            }
        }

        vestings[username]!!.remove(vestingEntry)
        vestingHistory[username]!!.add(vestingEntry)
    }
}
