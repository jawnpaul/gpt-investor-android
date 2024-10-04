package com.thejawnpaul.gptinvestor.core.database

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange

class Converters {

    private val moshi =
        Moshi.Builder().addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()

    private val priceChangeAdapter: JsonAdapter<PriceChange> =
        moshi.adapter(PriceChange::class.java)

    @TypeConverter
    fun stringToPriceChange(string: String): PriceChange? {
        return priceChangeAdapter.fromJson(string)
    }

    @TypeConverter
    fun priceChangeToString(priceChange: PriceChange): String {
        return priceChangeAdapter.toJson(priceChange)
    }
}