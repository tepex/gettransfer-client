package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.model.Point

interface AddressRepository
{
	fun getAddressByLocation(point: Point): String?
}
