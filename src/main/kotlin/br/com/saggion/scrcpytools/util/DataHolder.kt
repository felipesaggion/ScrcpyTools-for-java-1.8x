package br.com.saggion.scrcpytools.util

import br.com.saggion.scrcpytools.domain.Device

class DataHolder {
    lateinit var device: Device
    companion object {
        val instance = DataHolder()
    }
}
