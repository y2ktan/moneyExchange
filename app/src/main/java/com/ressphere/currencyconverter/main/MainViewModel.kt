package com.ressphere.currencyconverter.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ressphere.currencyconverter.data.models.Rates
import com.ressphere.currencyconverter.utils.DispatcherProvider
import com.ressphere.currencyconverter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToLong

@HiltViewModel
class MainViewModel @Inject constructor(
   private val repository: MainRepository,
   private val dispatcher: DispatcherProvider
): ViewModel() {
    sealed class CurrencyEvent {
        class Success(val resultString: String): CurrencyEvent()
        class Failure(val errorString: String): CurrencyEvent()
        object Loading: CurrencyEvent()
        object Empty: CurrencyEvent()
    }

    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion = _conversion.asStateFlow()

    fun convert(
        amountStr:String,
        from: String,
        to:String
    ) {
        val fromAmount = amountStr.toFloatOrNull()
        fromAmount?.let {
            viewModelScope.launch(dispatcher.io) {
                _conversion.value = CurrencyEvent.Loading
                val ratesResp = repository.getRates(from)
                if(ratesResp is Resource.Error) {
                    _conversion.value = CurrencyEvent.Failure(ratesResp.message?:"failed on resp")
                }

                if(ratesResp is Resource.Success) {
                    val rates = ratesResp.data!!.rates
                    val rate = getRateForCurrency(to, rates)
                    if(rate == null) {
                        _conversion.value = CurrencyEvent.Failure("Unexpected error")
                    } else {
                        val convertedCurrency = (fromAmount * rate * 100) / 100
                        val twoDecimalFormat = "%.2f".format(convertedCurrency)
                        _conversion.value = CurrencyEvent.Success(
                            "$fromAmount $from = $convertedCurrency $twoDecimalFormat"
                        )
                    }
                }
            }
        } ?: run {
            _conversion.value = CurrencyEvent.Failure("Amount must be a numeric type")
        }
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "CAD" -> rates.cAD
        "HKD" -> rates.hKD
        "ISK" -> rates.iSK
        "EUR" -> rates.eUR
        "PHP" -> rates.pHP
        "DKK" -> rates.dKK
        "HUF" -> rates.hUF
        "CZK" -> rates.cZK
        "AUD" -> rates.aUD
        "RON" -> rates.rON
        "SEK" -> rates.sEK
        "IDR" -> rates.iDR
        "INR" -> rates.iNR
        "BRL" -> rates.bRL
        "RUB" -> rates.rUB
        "HRK" -> rates.hRK
        "JPY" -> rates.jPY
        "THB" -> rates.tHB
        "CHF" -> rates.cHF
        "SGD" -> rates.sGD
        "PLN" -> rates.pLN
        "BGN" -> rates.bGN
        "CNY" -> rates.cNY
        "NOK" -> rates.nOK
        "NZD" -> rates.nZD
        "ZAR" -> rates.zAR
        "USD" -> rates.uSD
        "MXN" -> rates.mXN
        "ILS" -> rates.iLS
        "GBP" -> rates.gBP
        "KRW" -> rates.kRW
        "MYR" -> rates.mYR
        else -> null
    }
}