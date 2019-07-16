package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.ui.OffersActivity.Companion.PHOTO_CORNER
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.helpers.ScrollGalleryInflater

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_vehicle_photos.*

class VehiclePhotosView @JvmOverloads constructor(
    context: Context,
    attribute: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attribute, defStyleAttr), LayoutContainer {

    override val containerView: View? = LayoutInflater.from(context).inflate(R.layout.view_vehicle_photos, this, true)

    fun setPhotos(transportTypeResId: Int = 0, photos: List<String>? = null) {
        photos_container_bs.removeAllViews()
        iv_single_photo.isVisible = false
        sv_photos.isVisible = false
        if (!photos.isNullOrEmpty() && photos.size > 1) {
            addMultiplePhotos(photos)
        } else {
            addSinglePhoto(transportTypeResId, photos?.firstOrNull())
        }
    }

    private fun addMultiplePhotos(photos: List<String>) {
        photos_container_bs.removeAllViews()
        sv_photos.isVisible = true
        val size = getPhotoSize()
        inflatePhotoScrollView(photos.size)
        for (i in 0 until photos_container_bs.childCount) {
            Glide.with(this)
                .load(photos[i])
                .apply(
                    RequestOptions().transform(
                        CenterCrop(),
                        RoundedCorners(Utils.dpToPxInt(context, PHOTO_CORNER))
                    ).override(size.first, size.second)
                ).into(photos_container_bs.getChildAt(i) as ImageView)
        }
    }

    private fun addSinglePhoto(resId: Int = 0, path: String? = null) {
        iv_single_photo.isVisible = true
        Glide.with(this)
            .load(path ?: resId)
            .apply(RequestOptions().transform(CenterInside(), RoundedCorners(Utils.dpToPxInt(context, PHOTO_CORNER))))
            .into(iv_single_photo)
    }

    private fun inflatePhotoScrollView(imagesCount: Int) {
        ScrollGalleryInflater.addImageViews(imagesCount, photos_container_bs)
    }

    private fun getPhotoSize(): Pair<Int, Int> {
        val imgHorizontalMargins = resources.getDimensionPixelSize(R.dimen.bottom_sheet_offer_details_margin_16dp)
        val imgWidth = resources.displayMetrics.widthPixels - imgHorizontalMargins * 2
        val imgHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_offer_details_sv_photo_height)
        return Pair(imgWidth, imgHeight)
    }

    fun hidePhotos() {
        photos_container_bs.removeAllViews()
        iv_single_photo.isVisible = false
        sv_photos.isVisible = false
    }
}
