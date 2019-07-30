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
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    private lateinit var multiplePhotosSize: Pair<Int, Int>
    private var singlePhotoHeight = 0

    override val containerView: View? = LayoutInflater.from(context).inflate(R.layout.view_vehicle_photos, this, true)

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.VehiclePhotosView)

            multiplePhotosSize = Pair(
                ta.getDimensionPixelSize(R.styleable.VehiclePhotosView_multiple_photos_width,
                    resources.getDimensionPixelSize(R.dimen.bottom_sheet_offer_details_multiple_photos_width)),
                ta.getDimensionPixelSize(R.styleable.VehiclePhotosView_multiple_photos_height,
                    resources.getDimensionPixelSize(R.dimen.bottom_sheet_offer_details_multiple_photos_height))
            )

            singlePhotoHeight = ta.getDimensionPixelSize(R.styleable.VehiclePhotosView_single_photo_height,
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_offer_details_single_photo_height))

            ta.recycle()
        }
    }

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
        inflatePhotoScrollView(photos.size)
        for (i in 0 until photos_container_bs.childCount) {
            Glide.with(this)
                .load(photos[i])
                .apply(RequestOptions().transform(
                    CenterCrop(),
                    RoundedCorners(Utils.dpToPxInt(context, PHOTO_CORNER)))
                ).into(photos_container_bs.getChildAt(i) as ImageView)
        }
    }

    private fun addSinglePhoto(resId: Int = 0, path: String? = null) {
        iv_single_photo.isVisible = true
        if (path != null) iv_single_photo.layoutParams.apply { height = singlePhotoHeight }
        Glide.with(this)
            .load(path ?: resId)
            .apply(RequestOptions().transform(
                if (path != null) CenterCrop() else CenterInside(),
                RoundedCorners(Utils.dpToPxInt(context, PHOTO_CORNER)))
            ).into(iv_single_photo)
    }

    private fun inflatePhotoScrollView(imagesCount: Int) {
        ScrollGalleryInflater.addImageViews(imagesCount, multiplePhotosSize, photos_container_bs)
    }

    fun hidePhotos() {
        photos_container_bs.removeAllViews()
        iv_single_photo.isVisible = false
        sv_photos.isVisible = false
    }
}
