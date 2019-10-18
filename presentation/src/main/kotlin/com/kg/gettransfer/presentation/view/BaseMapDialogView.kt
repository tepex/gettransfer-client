package com.kg.gettransfer.presentation.view

import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.presentation.model.RouteModel

interface BaseMapDialogView : BaseView {
    fun setRoute(routeModel: RouteModel?, from: String, startPoint: LatLng)
}
