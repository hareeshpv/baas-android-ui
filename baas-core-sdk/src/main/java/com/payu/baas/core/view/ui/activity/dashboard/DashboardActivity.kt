package com.payu.baas.core.view.ui.activity.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityDashboardBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.model.OfferDetails
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.*
import com.payu.baas.core.util.enums.ProfileWebViewEnum
import com.payu.baas.core.view.callback.ItemClickListener
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.address.AdressActivity
import com.payu.baas.core.view.ui.activity.advance.AdvanceActivity
import com.payu.baas.core.view.ui.activity.cardUpdatePin.UpadatePinActivity
import com.payu.baas.core.view.ui.activity.cards.CardsActivity
import com.payu.baas.core.view.ui.activity.moneyTransfer.MoneyTransferActivity
import com.payu.baas.core.view.ui.activity.passbook.PassBookActivity
import com.payu.baas.core.view.ui.activity.profile.UserProfileActivity
import com.payu.baas.core.view.ui.activity.webView.WebViewActivity
import com.payu.baas.core.view.ui.common.AddMoneyBottomSheetDialogFragment
import com.payu.baas.core.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*


class DashboardActivity : BaseActivity() {

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: DashboardViewModel
    var mobileNumber: String? = null

    lateinit var tipsAdapter: PagerAdapter
    private var offersAdapter: OffersAdapter? = null

    private var accountDetails: GetAccountDetailsResponse? = null
    private var notificationDetails: NotificationResponse? = null
    private var cardDetails: CardFulfilmentResponse? = null
    private var cardPinStatusDetails: GetCardPinStatusResponse? = null
    private var cardKitStatusDetails: GetValidateCardKitStatusResponse? = null

    private var setCardPinLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.getCardFulfillment()
            }
        }
    private var cardReOrderLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.cardReOrder()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getAccountBalanceDetails()
    }

    private fun setupUI() {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_ADD_PAGE_ACCESS,
            BaaSConstantsUI.CL_ADD_PAGE_ACCESS_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        sheetBehavior = BottomSheetBehavior.from(binding.llOffersDeals)
        sheetBehavior.isDraggable = false

        binding.tvOffersAndDeals.setOnClickListener {
            handleBottomSheetVisibility()
        }
        binding.viewBackground.setOnClickListener {
            handleBottomSheetVisibility()
        }
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.viewBackground.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.viewBackground.visibility = View.VISIBLE
                binding.viewBackground.alpha = slideOffset
            }
        })

        // tips
        tipsAdapter = TipsAdapter(object : ItemClickListener {
            override fun onItemClicked(model: Any) {
            }
        }, this)
        binding.vpTips.adapter = tipsAdapter
        binding.tlTips.setupWithViewPager(binding.vpTips, true)

        // offers
        val layoutManager = LinearLayoutManager(this)
        binding.rvOffers.layoutManager = layoutManager
        binding.rvOffers.setHasFixedSize(true)
        offersAdapter =
            OffersAdapter(object : ItemClickListener {
                override fun onItemClicked(model: Any) {
                    val offerDetails = model as OfferDetails

                    if (offerDetails != null) {
                        if (!offerDetails.ctaUrl.isNullOrEmpty()) {
                            val mBundle = Bundle()
                            mBundle.putString(
                                BaaSConstantsUI.USER_PROFILE_FLAG,
                                ProfileWebViewEnum.OFFERS.getValue()
                            )
                            mBundle.putString(BaaSConstantsUI.USER_URL, offerDetails.ctaUrl)
                            callNextScreen(
                                Intent(this@DashboardActivity, WebViewActivity::class.java), mBundle
                            )

                        }
                    }
                }
            }, this)
        binding.rvOffers.adapter = offersAdapter
    }

    private fun setupObserver() {
        viewModel.getS3BucketUrlObs().observe(this) {
            parseResponse(it, ApiName.GET_S3_BUCKET_LINK)
        }
        viewModel.getUserDetailsObs().observe(this) {
            parseResponse(it, ApiName.ZENDESK_CREDENTIALS)
        }
        viewModel.getUserDetailsObs().observe(this) {
            parseResponse(it, ApiName.GET_USER_DETAILS)
        }

        viewModel.getAccountBalanceDetailsObs().observe(this) {
            parseResponse(it, ApiName.GET_ACCOUNT_BALANCE_DETAILS)
        }

        viewModel.getAccountDetailsObs().observe(this) {
            parseResponse(it, ApiName.GET_ACCOUNT_DETAILS)
        }

        viewModel.getNotificationObs().observe(this) {
            parseResponse(it, ApiName.GET_NOTIFICATIONS)
        }

        viewModel.getCardImageObs().observe(this) {
            parseResponse(it, ApiName.CARD_IMAGE)
        }

        viewModel.getTipsObs().observe(this) {
            parseResponse(it, ApiName.GET_TIPS)
        }

        viewModel.getOffersAndDealsObs().observe(this) {
            parseResponse(it, ApiName.GET_OFFER)
        }

        viewModel.getCardFulfilmentObs().observe(this) {
            parseResponse(it, ApiName.CARD_FULFILMENT)
        }

        viewModel.getCardPinStatusObs().observe(this) {
            parseResponse(it, ApiName.GET_PIN_STATUS)
        }

        viewModel.getCardValidateKitStatusObs().observe(this) {
            parseResponse(it, ApiName.GET_VALIDATE_CARD_KIT_STATUS)
        }

        viewModel.getHelpObs().observe(this) {
            parseResponse(it, ApiName.HELP)
        }

        viewModel.getValidateCardKitObs().observe(this) {
            parseResponse(it, ApiName.VALIDATE_CARD_KIT)
        }

        viewModel.getCardReOrderObs().observe(this) {
            parseResponse(it, ApiName.CARD_REORDER)
        }

    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.GET_S3_BUCKET_LINK -> {
                        it.data?.let {
                            if (it is GetS3BucketLinkResponse) {
                            }
                        }
                    }
                    ApiName.GET_USER_DETAILS -> {
                        it.data?.let {
                            if (it is GetUserDetailsResponse) {
                                invalidateUserDetailsUI(it)
                            }
                        }
                    }
                    ApiName.GET_ACCOUNT_BALANCE_DETAILS -> {
                        it.data?.let {
                            if (it is GetAccountBalanceDetailsResponse) {
                                invalidateAccountBalanceUI(it)
                            }
                        }
                    }
                    ApiName.GET_ACCOUNT_DETAILS -> {
                        it.data?.let {
                            if (it is GetAccountDetailsResponse) {
                                invalidateAccountDetailsUI(it)
                            }
                        }
                    }
                    ApiName.GET_NOTIFICATIONS -> {
                        it.data?.let {
                            if (it is NotificationResponse) {
                                invalidateNotificationsUI(it)
                            }
                        }
                    }
                    ApiName.CARD_IMAGE -> {
                        it.data?.let {
                            if (it is CardImageResponse) {
                                invalidateCardUI(it)
                            }
                        }
                    }
                    ApiName.GET_TIPS -> {
                        it.data?.let {
                            if (it is TipResponse) {
                                invalidateTipsUI(it)
                            }
                        }
                    }
                    ApiName.GET_OFFER -> {
                        it.data?.let {
                            if (it is OffersResponse) {
                                invalidateOffersUI(it)
                            }
                        }
                    }
                    ApiName.CARD_FULFILMENT -> {
                        it.data?.let {
                            if (it is CardFulfilmentResponse) {
                                cardDetails = it
                                invalidateCardTrackingUI()
                            }
                        }
                    }
                    ApiName.GET_PIN_STATUS -> {
                        it.data?.let {
                            if (it is GetCardPinStatusResponse) {
                                cardPinStatusDetails = it
                                invalidateCardTrackingUI()
                            }
                        }
                    }
                    ApiName.GET_VALIDATE_CARD_KIT_STATUS -> {
                        it.data?.let {
                            if (it is GetValidateCardKitStatusResponse) {
                                cardKitStatusDetails = it
                                invalidateCardTrackingUI()
                            }
                        }
                    }
                    ApiName.HELP -> {
                        it.data?.let {
                            if (it is HelpResponse) {
                                navigateToWebview(it)
                            }
                        }
                    }
                    ApiName.VALIDATE_CARD_KIT -> {
                        it.data?.let {
                            if (it is ValidateCardKitResponse) {
                                viewModel.getCardFulfillment()
                                if (!it.validated!!) {
                                    // open ticket flow from here
                                    binding.tvHelp.performClick()
                                }
                            }
                        }
                    }
                    ApiName.CARD_REORDER -> {
                        it.data?.let {
                            if (it is CardReorderResponse) {
                                SessionManager.getInstance(this).cardImage = ""
                                viewModel.getCardFulfillment()
                            }
                        }
                    }
                }
            }
            Status.LOADING -> {
                showProgress("")
            }
            Status.ERROR -> {
                hideProgress()
                var errorResponse = it.data as ErrorResponse
                if (!(it.message.isNullOrEmpty()) && (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
                            || it.message!!.contains(BaaSConstantsUI.DEVICE_BINDING_FAILED))
                ) {
                    viewModel.reGenerateAccessToken(false)
                } else if (errorResponse.errorCode >= BaaSConstantsUI.TECHINICAL_ERROR_CODE
                    && errorResponse.errorCode < BaaSConstantsUI.TECHINICAL_ERROR_CROSSED_CODE
                ) {
                    viewModel.baseCallBack!!.showTechinicalError()
                } else {
                    when (apiName) {
                        ApiName.GET_S3_BUCKET_LINK -> {
                        }
                        ApiName.GET_USER_DETAILS -> {
                            invalidateUserDetailsUI(null)
                        }
                        ApiName.GET_ACCOUNT_BALANCE_DETAILS -> {
                            invalidateAccountBalanceUI(null)
                        }
                        ApiName.GET_ACCOUNT_DETAILS -> {
                            invalidateAccountDetailsUI(null)
                        }
                        ApiName.GET_NOTIFICATIONS -> {
                            invalidateNotificationsUI(null)
                        }
                        ApiName.CARD_IMAGE -> {
                            invalidateCardUI(null)
                        }
                        ApiName.GET_TIPS -> {
                            invalidateTipsUI(null)
                        }
                        ApiName.GET_OFFER -> {
                            invalidateOffersUI(null)
                        }
                        ApiName.CARD_FULFILMENT -> {
                            invalidateCardTrackingUI()
                        }
                        ApiName.GET_PIN_STATUS -> {
                            invalidateCardTrackingUI()
                        }
                        ApiName.GET_VALIDATE_CARD_KIT_STATUS -> {
                            invalidateCardTrackingUI()
                        }
                        ApiName.HELP -> {
                            navigateToWebview(null)
                        }
                        ApiName.VALIDATE_CARD_KIT -> {
                        }
                        ApiName.CARD_REORDER -> {
                        }
                    }
                    var msg = ""
                    try {
                        var errorUiRes = JsonUtil.toObject(
                            it.message,
                            ErrorResponseUI::class.java
                        ) as ErrorResponseUI
                        msg = errorUiRes.userMessage!!
                    } catch (e: Exception) {
                        msg = it.message!!
                    }
                    SimpleCustomSnackbar.showSwitchMsg.make(
                        binding.nested,
                        msg,
                        Snackbar.LENGTH_LONG,
                        false
                    )?.show()
                }

//                hideProgress()
//                if (!(it.message.isNullOrEmpty()) && (it.message.contains(BaaSConstantsUI.INVALID_ACCESS_TOKEN)
//                            || it.message!!.contains(BaaSConstantsUI.DEVICE_BINDING_FAILED))
//                ) {
//                    viewModel.reGenerateAccessToken(false)
//                } else {
//                    when (apiName) {
//                        ApiName.GET_S3_BUCKET_LINK -> {
//                        }
//                        ApiName.GET_USER_DETAILS -> {
//                            invalidateUserDetailsUI(null)
//                        }
//                        ApiName.GET_ACCOUNT_BALANCE_DETAILS -> {
//                            invalidateAccountBalanceUI(null)
//                        }
//                        ApiName.GET_ACCOUNT_DETAILS -> {
//                            invalidateAccountDetailsUI(null)
//                        }
//                        ApiName.GET_NOTIFICATIONS -> {
//                            invalidateNotificationsUI(null)
//                        }
//                        ApiName.CARD_IMAGE -> {
//                            invalidateCardUI(null)
//                        }
//                        ApiName.GET_TIPS -> {
//                            invalidateTipsUI(null)
//                        }
//                        ApiName.GET_OFFER -> {
//                            invalidateOffersUI(null)
//                        }
//                        ApiName.CARD_FULFILMENT -> {
//                            invalidateCardTrackingUI()
//                        }
//                        ApiName.GET_PIN_STATUS -> {
//                            invalidateCardTrackingUI()
//                        }
//                        ApiName.GET_VALIDATE_CARD_KIT_STATUS -> {
//                            invalidateCardTrackingUI()
//                        }
//                        ApiName.HELP -> {
//                            navigateToWebview(null)
//                        }
//                        ApiName.VALIDATE_CARD_KIT -> {
//                        }
//                        ApiName.CARD_REORDER -> {
//                        }
//                    }
////                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                    it.message?.let { it1 ->
//                        showSnackbarOnSwitchAction(
//                            it1,
//                            false
//                        )
//                    }
//                }
            }
            Status.RETRY -> {
                hideProgress()
            }
        }
    }


    private fun invalidateUserDetailsUI(userDetails: GetUserDetailsResponse?) {
        if (userDetails != null) {

            var profilePicture = ""
            userDetails.selfieLink?.let {
                profilePicture = it
            }
            if (!profilePicture.isNullOrEmpty()) {
                Glide.with(this)
                    .load(profilePicture)
                    .error(R.drawable.delivery_boy)
                    .into(binding.ivProfilePic)
            }

            var userName = "-"
            userDetails.name?.let {
                userName = it
            }

            binding.tvUserName.text = userName
        } else {
            binding.tvUserName.text = "-"
        }
        SessionManagerUI.getInstance(this).userName = binding.tvUserName.text.trim().toString()
    }

    private fun invalidateAccountBalanceUI(accountBalanceDetails: GetAccountBalanceDetailsResponse?) {
        if (accountBalanceDetails != null) {

            var prepaidBalance = ""
            accountBalanceDetails.prepaidBalance?.let {
                prepaidBalance = it
            }
            var totalAvailableBalance = ""
            accountBalanceDetails.totalAvailableBalance?.let {
                totalAvailableBalance = it
            }
            var advanceBalance = ""
            accountBalanceDetails.advanceBalance?.let {
                advanceBalance = it
            }

            binding.tvExpenseLimitAmount.text = totalAvailableBalance
            binding.tvAccountBalanceAmount.text = prepaidBalance
            binding.tvAdvanceAllowedAmount.text = advanceBalance

            if (totalAvailableBalance.isEmpty()
                || totalAvailableBalance.toDouble() == 0.0
            ) {
                showNoBalanceUI()
            } else {
                showBalanceUI()
            }

        } else {
            showNoBalanceUI()
        }
    }

    private fun showBalanceUI() {
        binding.llNoBalanceInfo.visibility = View.GONE
        binding.llDummyNoBalanceInfo.visibility = View.GONE
        binding.llDummyAccountNoBalanceInfo.visibility = View.GONE

        binding.llBalanceInfo.visibility = View.VISIBLE
        binding.llDummyBalanceInfo.visibility = View.VISIBLE
        binding.llDummyAccountBalanceInfo.visibility = View.VISIBLE
    }

    private fun showNoBalanceUI() {
        binding.tvExpenseLimitAmount.text = "0"
        binding.tvAccountBalanceAmount.text = "0"
        binding.tvAdvanceAllowedAmount.text = "0"

        binding.llNoBalanceInfo.visibility = View.VISIBLE
        binding.llDummyNoBalanceInfo.visibility = View.VISIBLE
        binding.llDummyAccountNoBalanceInfo.visibility = View.VISIBLE

        binding.llBalanceInfo.visibility = View.GONE
        binding.llDummyBalanceInfo.visibility = View.GONE
        binding.llDummyAccountBalanceInfo.visibility = View.GONE
    }

    private fun invalidateAccountDetailsUI(accountDetails: GetAccountDetailsResponse?) {
        this.accountDetails = accountDetails
    }

    private fun invalidateNotificationsUI(notificationDetails: NotificationResponse?) {

        if (notificationDetails != null && notificationDetails.notificationList.size > 0
            && !notificationDetails.notificationList[0].notificationText.isNullOrEmpty()
        ) {
            // with notifications
            binding.llNotificationInfo.visibility = View.VISIBLE
            binding.llDummyNotificationInfo.visibility = View.VISIBLE

            this.notificationDetails = notificationDetails

            // notification details
            Glide.with(this)
                .load(SessionManager.getInstance(this).s3BucketUrl + notificationDetails.notificationList[0].icon)
                .error(R.drawable.delivery_boy)
                .into(binding.ivNotificationIcon)

            binding.tvNotification.text = notificationDetails.notificationList[0].notificationText
        } else {
            // without notifications
            hideNotificationUI()
        }
    }

    private fun hideNotificationUI() {
        binding.llNotificationInfo.visibility = View.GONE
        binding.llDummyNotificationInfo.visibility = View.GONE
    }

    private fun invalidateCardUI(cardDetails: CardImageResponse?) {
        if (cardDetails != null) {
            val decodedStringImage: String =
                Utils.decodeString(
                    SessionManager.getInstance(
                        this
                    ).cardImage!!
                )
            if (!decodedStringImage.isNullOrEmpty()) {
                Glide.with(this)
                    .load(decodedStringImage)
                    .error(R.drawable.card_bg)
                    .into(binding.ivCard)
            } else {
                binding.ivCard.setBackgroundResource(R.drawable.card_bg)
            }

        } else {
            binding.ivCard.setBackgroundResource(R.drawable.card_bg)
        }
    }

    private fun invalidateTipsUI(tipDetails: TipResponse?) {
        if (tipDetails != null && tipDetails.tipsList.size > 0) {
            // with tips
            binding.rlTips.visibility = View.VISIBLE

            (tipsAdapter as TipsAdapter).setList(tipDetails.tipsList)

            // TODO: testing
            /*var list = arrayListOf<TipDetails>()
            for (i in 1..3) {
                var tips = TipDetails()
                tips.tipText =
                    "Aap apni advance salary se roz bina surcharge diye petrol kharid skte ho. Aap apni advance salary se roz bina surcharge diye petrol kharid skte ho. Aap apni advance salary se roz bina surcharge diye petrol kharid skte ho. Aap apni advance salary se roz bina surcharge diye petrol kharid skte ho."

                list.add(tips)
            }
            (tipsAdapter as TipsAdapter).setList(list)*/

            //Give padding to tabs
            for (i in 0 until binding.tlTips.tabCount) {
                val tab: View = (binding.tlTips.getChildAt(0) as ViewGroup).getChildAt(i)
                val p: ViewGroup.MarginLayoutParams =
                    tab.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(6, 0, 6, 0)
                tab.requestLayout()
            }
        } else {
            // without tips
            binding.rlTips.visibility = View.GONE
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun invalidateOffersUI(offerDetails: OffersResponse?) {
        if (offerDetails != null && offerDetails.offerList.size > 0) {

            val offersAndDeals = getString(R.string.offer_and_deals_link)
            val completeString = getString(R.string.no_account_balance_with_offers, offersAndDeals)
            val startIndex = completeString.indexOf(offersAndDeals)
            val endIndex = startIndex + offersAndDeals.length
            val spannableStringBuilder = SpannableStringBuilder(completeString)
            val clickOnOffers: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    handleBottomSheetVisibility()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = ContextCompat.getColor(activity!!, R.color.green_light)
                    ds.isUnderlineText = true
                    UtilsUI.setGradientColorToLinks(ds, offersAndDeals)
                }
            }
            spannableStringBuilder.setSpan(
                clickOnOffers,
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.tvNoBalanceInfo.text = spannableStringBuilder
            binding.tvNoBalanceInfo.movementMethod = LinkMovementMethod.getInstance()

            binding.llOffersDeals.visibility = View.VISIBLE
            offersAdapter?.setList(offerDetails.offerList)
        } else {
            // without offers
            binding.tvNoBalanceInfo.text = getString(R.string.no_account_balance_info)

            binding.llOffersDeals.visibility = View.GONE
        }
    }


    private fun invalidateCardTrackingUI() {
        if ((cardDetails != null && cardDetails!!.cardFulfilmentList.isNotEmpty())
            && cardPinStatusDetails != null
            && cardKitStatusDetails != null
        ) {
            var cardDeliveryStatus = ""
            cardDetails!!.cardFulfilmentList[cardDetails!!.cardFulfilmentList.size - 1].status?.let {
                cardDeliveryStatus = it
            }

            var isCardPinSet = ""
            cardPinStatusDetails!!.status?.let {
                isCardPinSet = it
            }

            var isCardReceived = ""
            cardKitStatusDetails!!.message?.let {
                isCardReceived = it
            }

            if (isCardPinSet.lowercase() == "1") {
                // hide everything
                binding.llCardDeliveryStatus.visibility = View.GONE
                binding.llCardActivation.visibility = View.GONE

                return
            }

            if (isCardReceived.lowercase() == "yes") {
                // show card activation layout

                binding.llCardDeliveryStatus.visibility = View.GONE
                binding.llCardActivation.visibility = View.VISIBLE

                return
            }

            if (cardDeliveryStatus.lowercase() != "delivered"
                && isCardPinSet.lowercase() == "0"
                && isCardReceived.lowercase() == "no"
            ) {
                // show card tracking layout

                binding.llCardDeliveryStatus.visibility = View.VISIBLE
                binding.llCardActivation.visibility = View.GONE

                // show card tracking status layout
                binding.llCardDeliveryStatusTrack.visibility = View.VISIBLE
                when (cardDetails!!.cardFulfilmentList.size) {
                    1 -> {
                        // show is card delivered text
                        binding.tvIsCardDelivered.visibility = View.VISIBLE
                        // hide return layout
                        binding.llCardReturned.visibility = View.GONE

                        /* Ordered layout */
                        binding.viewCardDeliveryStatusOrderedPending.visibility = View.GONE
                        binding.rlCardDeliveryStatusOrderedCompleted.visibility = View.GONE

                        binding.rlCardDeliveryStatusOrderedInTransist.visibility = View.VISIBLE

                        // show data
                        binding.tvCardDeliveryStatusOrdered.alpha = 1.0F
                        binding.tvCardDeliveryStatusOrdered.text =
                            cardDetails!!.cardFulfilmentList[0].status
                        binding.tvCardDeliveryStatusOrderedDate.text =
                            cardDetails!!.cardFulfilmentList[0].time

                        /* Dispatched layout */
                        binding.viewCardDeliveryStatusDispatchedPending.visibility = View.VISIBLE

                        binding.rlCardDeliveryStatusDispatchedInTransist.visibility = View.GONE
                        binding.rlCardDeliveryStatusDispatchedCompleted.visibility = View.GONE
                        binding.tvCardDeliveryStatusDispatched.alpha = 0.6F

                        /* Delivered layout */
                        binding.viewCardDeliveryStatusDeliveredPending.visibility = View.VISIBLE

                        binding.rlCardDeliveryStatusDeliveredInTransist.visibility = View.GONE
                        binding.rlCardDeliveryStatusDeliveredCompleted.visibility = View.GONE
                        binding.tvCardDeliveryStatusDelivered.alpha = 0.6F

                        /* tracking lines */
                        binding.viewOrderedToDispatched.setBackgroundResource(R.drawable.bg_horizontal_dashed_line_river_bed)
                        binding.viewDispatchedToDelivered.setBackgroundResource(R.drawable.bg_horizontal_dashed_line_river_bed)

                    }
                    2 -> {
                        // show is card delivered text
                        binding.tvIsCardDelivered.visibility = View.VISIBLE
                        // hide return layout
                        binding.llCardReturned.visibility = View.GONE

                        /* Ordered layout */
                        binding.viewCardDeliveryStatusOrderedPending.visibility = View.GONE
                        binding.rlCardDeliveryStatusOrderedInTransist.visibility = View.GONE

                        binding.rlCardDeliveryStatusOrderedCompleted.visibility = View.VISIBLE

                        // show data
                        binding.tvCardDeliveryStatusOrdered.alpha = 1.0F
                        binding.tvCardDeliveryStatusOrdered.text =
                            cardDetails!!.cardFulfilmentList[0].status
                        binding.tvCardDeliveryStatusOrderedDate.text =
                            cardDetails!!.cardFulfilmentList[0].time

                        /* Dispatched layout */
                        binding.viewCardDeliveryStatusDispatchedPending.visibility = View.GONE
                        binding.rlCardDeliveryStatusDispatchedCompleted.visibility = View.GONE

                        binding.rlCardDeliveryStatusDispatchedInTransist.visibility = View.VISIBLE

                        // show data
                        binding.tvCardDeliveryStatusDispatched.alpha = 1.0F
                        binding.tvCardDeliveryStatusDispatched.text =
                            cardDetails!!.cardFulfilmentList[1].status
                        binding.tvCardDeliveryStatusDispatchedDate.text =
                            cardDetails!!.cardFulfilmentList[1].time

                        /* Delivered layout */
                        binding.viewCardDeliveryStatusDeliveredPending.visibility = View.VISIBLE

                        binding.rlCardDeliveryStatusDeliveredInTransist.visibility = View.GONE
                        binding.rlCardDeliveryStatusDeliveredCompleted.visibility = View.GONE
                        binding.tvCardDeliveryStatusDelivered.alpha = 0.6F

                        /* tracking lines */
                        binding.viewOrderedToDispatched.setBackgroundResource(R.drawable.bg_horizontal_dashed_line_jade)
                        binding.viewDispatchedToDelivered.setBackgroundResource(R.drawable.bg_horizontal_dashed_line_river_bed)

                    }
                    3 -> {
                        // hide is card delivered text
                        binding.tvIsCardDelivered.visibility = View.GONE
                        // show return layout
                        binding.llCardReturned.visibility = View.VISIBLE

                        /* Ordered layout */
                        binding.viewCardDeliveryStatusOrderedPending.visibility = View.GONE
                        binding.rlCardDeliveryStatusOrderedInTransist.visibility = View.GONE

                        binding.rlCardDeliveryStatusOrderedCompleted.visibility = View.VISIBLE

                        // show data
                        binding.tvCardDeliveryStatusOrdered.alpha = 1.0F
                        binding.tvCardDeliveryStatusOrdered.text =
                            cardDetails!!.cardFulfilmentList[0].status
                        binding.tvCardDeliveryStatusOrderedDate.text =
                            cardDetails!!.cardFulfilmentList[0].time

                        /* Dispatched layout */
                        binding.viewCardDeliveryStatusDispatchedPending.visibility = View.GONE
                        binding.rlCardDeliveryStatusDispatchedInTransist.visibility = View.GONE

                        binding.rlCardDeliveryStatusDispatchedCompleted.visibility = View.VISIBLE

                        // show data
                        binding.tvCardDeliveryStatusDispatched.alpha = 1.0F
                        binding.tvCardDeliveryStatusDispatched.text =
                            cardDetails!!.cardFulfilmentList[1].status
                        binding.tvCardDeliveryStatusDispatchedDate.text =
                            cardDetails!!.cardFulfilmentList[1].time

                        /* RTO (Re-Order) */
                        /* Delivered layout */
                        binding.viewCardDeliveryStatusDeliveredPending.visibility = View.GONE
                        binding.rlCardDeliveryStatusDeliveredCompleted.visibility = View.GONE

                        binding.rlCardDeliveryStatusDeliveredInTransist.visibility = View.VISIBLE

                        // show data
                        binding.rlCardDeliveryStatusDeliveredInTransist.rotation = 180F
                        binding.tvCardDeliveryStatusDelivered.alpha = 1.0F
                        binding.tvCardDeliveryStatusDelivered.text =
                            cardDetails!!.cardFulfilmentList[2].status
                        binding.tvCardDeliveryStatusDeliveredDate.text =
                            cardDetails!!.cardFulfilmentList[2].time

                        /* tracking lines */
                        binding.viewOrderedToDispatched.setBackgroundResource(R.drawable.bg_horizontal_dashed_line_jade)
                        binding.viewDispatchedToDelivered.setBackgroundResource(R.drawable.bg_horizontal_dashed_line_jade)
                    }
                }

            } else if (cardDeliveryStatus.lowercase() == "delivered"
                && isCardPinSet.lowercase() == "0"
                && isCardReceived.lowercase() == "no"
            ) {

                binding.llCardDeliveryStatus.visibility = View.VISIBLE
                binding.llCardActivation.visibility = View.GONE

                // show card tracking status layout
                binding.llCardDeliveryStatusTrack.visibility = View.VISIBLE

                binding.tvIsCardDelivered.visibility = View.GONE
                binding.tvIsCardDeliveredConfirmed.visibility = View.VISIBLE
                binding.tvIsCardNotDelivered.visibility = View.VISIBLE

                // show return layout
                binding.llCardReturned.visibility = View.GONE

                //Ordered layout
                binding.viewCardDeliveryStatusOrderedPending.visibility = View.GONE
                binding.rlCardDeliveryStatusOrderedInTransist.visibility = View.GONE

                binding.rlCardDeliveryStatusOrderedCompleted.visibility = View.VISIBLE

                // show data
                binding.tvCardDeliveryStatusOrdered.alpha = 1.0F
                binding.tvCardDeliveryStatusOrdered.text =
                    cardDetails!!.cardFulfilmentList[0].status
                binding.tvCardDeliveryStatusOrderedDate.text =
                    cardDetails!!.cardFulfilmentList[0].time

                // Dispatched layout
                binding.viewCardDeliveryStatusDispatchedPending.visibility = View.GONE
                binding.rlCardDeliveryStatusDispatchedInTransist.visibility = View.GONE

                binding.rlCardDeliveryStatusDispatchedCompleted.visibility = View.VISIBLE

                // show data
                binding.tvCardDeliveryStatusDispatched.alpha = 1.0F
                binding.tvCardDeliveryStatusDispatched.text =
                    cardDetails!!.cardFulfilmentList[1].status
                binding.tvCardDeliveryStatusDispatchedDate.text =
                    cardDetails!!.cardFulfilmentList[1].time

                //RTO (Re-Order)
                //Delivered layout
                binding.viewCardDeliveryStatusDeliveredPending.visibility = View.GONE
                binding.rlCardDeliveryStatusDeliveredCompleted.visibility = View.GONE

                binding.rlCardDeliveryStatusDeliveredInTransist.visibility = View.VISIBLE

                // show data
                //binding.rlCardDeliveryStatusDeliveredInTransist.rotation = 180F
                binding.tvCardDeliveryStatusDelivered.alpha = 1.0F
                binding.tvCardDeliveryStatusDelivered.text =
                    cardDetails!!.cardFulfilmentList[2].status
                binding.tvCardDeliveryStatusDeliveredDate.text =
                    cardDetails!!.cardFulfilmentList[2].time

                //tracking lines
                binding.viewOrderedToDispatched.setBackgroundResource(R.drawable.bg_horizontal_dashed_line_jade)
                binding.viewDispatchedToDelivered.setBackgroundResource(R.drawable.bg_horizontal_dashed_line_jade)

                /*// hide everything and open ticket flow

                binding.llCardDeliveryStatus.visibility = View.GONE
                binding.llCardActivation.visibility = View.GONE

                // open ticket flow from here
                Toast.makeText(this, "Open ticket flow", Toast.LENGTH_SHORT).show()*/
            }

        } else {
            binding.llCardDeliveryStatus.visibility = View.GONE
            binding.llCardActivation.visibility = View.GONE
        }
    }

    private fun navigateToWebview(helpDetails: HelpResponse?) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_USER_HELP_HOME,
            BaaSConstantsUI.CL_USER_HELP_HOME_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        if (helpDetails != null && !helpDetails.value.isNullOrEmpty()) {
            val mBundle = Bundle()
            mBundle.putString(
                BaaSConstantsUI.USER_PROFILE_FLAG,
                ProfileWebViewEnum.HELP.getValue()
            )
            mBundle.putString(BaaSConstantsUI.USER_URL, helpDetails.value)
            callNextScreen(
                Intent(this, WebViewActivity::class.java), mBundle
            )
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            DashboardViewModel.DashboardViewModelFactory(this, this)
        )[DashboardViewModel::class.java]

        binding.viewModel = viewModel
    }

    fun onProfileClicked(view: View) {
        callNextScreen(Intent(this, UserProfileActivity::class.java), null)
    }

    fun onAccountInfoClicked(view: View) {
    }

    fun onHelpClicked(view: View) {
        //viewModel.getHelp()
        launchZendeskSDK()
    }


    /**
     * Show Tooltip message when user click on Help (?) icon
     */
    fun onTooltipClicked(view: View) {
        UtilsUI.showPopupWindow(
            this@DashboardActivity,
            binding.llTooltip,
            getString(R.string.tooltip_msg_for_home)
        )
    }

    fun onNotificationClicked(view: View) {
        if (notificationDetails != null && notificationDetails!!.notificationList.size > 0) {
            val clickToAction = notificationDetails!!.notificationList[0].ctaURL

            if (!clickToAction.isNullOrEmpty()) {
                val mBundle = Bundle()
                mBundle.putString(
                    BaaSConstantsUI.USER_PROFILE_FLAG,
                    ProfileWebViewEnum.NOTIFICATIONS.getValue()
                )
                mBundle.putString(BaaSConstantsUI.USER_URL, clickToAction)
                callNextScreen(
                    Intent(this, WebViewActivity::class.java), mBundle
                )
            }
        }
    }

    fun onCardInfoClicked(view: View) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_USER_ACCESS_CARD_MANAGEMENT,
            BaaSConstantsUI.CL_USER_ACCESS_CARD_MANAGEMENT_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        callNextScreen(Intent(this, CardsActivity::class.java), null)

    }

    fun onPassbookClicked(view: View) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_USER_ACCESS_PASSBOOK,
            BaaSConstantsUI.CL_USER_ACCESS_PASSBOOK_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        callNextScreen(Intent(this, PassBookActivity::class.java), null)
    }

    fun onSendMoneyClicked(view: View) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_USER_ACCESS_MONEY_TRANSFER,
            BaaSConstantsUI.CL_USER_ACCESS_MONEY_TRANSFER_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        callNextScreen(Intent(this, MoneyTransferActivity::class.java), null)
    }

    fun onAdvanceAccountClicked(view: View) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_USER_ACCESSS_ADVANCE_PAGE,
            BaaSConstantsUI.CL_USER_ACCESSS_ADVANCE_PAGE_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        callNextScreen(Intent(this, AdvanceActivity::class.java), null)
    }

    fun onSetCardPinClicked(view: View) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_USER_SET_CARD_PIN,
            BaaSConstantsUI.CL_USER_SET_CARD_PIN_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        setCardPinLauncher.launch(Intent(this, UpadatePinActivity::class.java))
    }

    fun onCardReOrderClicked(view: View) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_USER_RE_ORDER_CARD,
            BaaSConstantsUI.CL_USER_RE_ORDER_CARD_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        val intent = Intent(this, AdressActivity::class.java)
        intent.putExtra(
            BaaSConstantsUI.ARGUMENTS_CARD_RE_ORDER,
            BaaSConstantsUI.ARGUMENTS_CARD_RE_ORDER
        )
        cardReOrderLauncher.launch(intent)
    }

    fun onAddMoneyClicked(view: View) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_ADD_MONEY_HOME,
            BaaSConstantsUI.CL_ADD_MONEY_HOME_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        var bundle = Bundle()
        bundle.putString(BaaSConstantsUI.ARGUMENTS_TITLE, getString(R.string.add_money))
        if (accountDetails != null) {
            bundle.putString(
                BaaSConstantsUI.ARGUMENTS_ACCOUNT_DETAILS,
                Gson().toJson(accountDetails)
            )
        }
        val fragment = AddMoneyBottomSheetDialogFragment.newInstance(bundle)
        fragment.show(supportFragmentManager, "add_money")
        fragment.isCancelable = true
    }

    fun onIsCardDeliveredClicked(view: View) {
        val fragment = CardDeliveredConfirmationBottomDialogFragment.newInstance()
        fragment.show(supportFragmentManager, "Card_Delivered_Confirmation")
        fragment.isCancelable = false
    }

    fun onConfirmCardDeliveredClicked(view: View) {
        cardDeliveredConfirmationDoneByUserClicked()
    }

    fun cardDeliveredConfirmationDoneByUserClicked() {
        viewModel.validateCardKit(true)
    }

    fun onIsCardNotDeliveredClicked(view: View) {
        /*showAlertWithMultipleCallBack(
            getString(R.string.are_you_sure_card_is_not_delivered),
            getString(R.string.if_yes_then_resend_card_will_take_more_days),
            getString(R.string.yes_agreed),
            getString(R.string.cancel),
            object : AlertDialogMultipleCallback {
                override fun onPositiveActionButtonClick() {
                    viewModel.validateCardKit(false)
                }

                override fun onNegativeActionButtonClick() {
                }
            }
        )*/
        viewModel.validateCardKit(false)
    }

    fun hideNotificationUI(view: View) {
        viewModel.baseCallBack!!.cleverTapUserHomeEvent(
            BaaSConstantsUI.CL_USER_CLOSE_ADVANCE_NOTIFICATION,
            BaaSConstantsUI.CL_USER_CLOSE_ADVANCE_NOTIFICATION_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        hideNotificationUI()
    }


    fun handleBottomSheetVisibility() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            val params =
                binding.llOffersDeals.layoutParams as CoordinatorLayout.LayoutParams
            params.setMargins(0, 100, 0, 0)
            binding.llOffersDeals.layoutParams = params

            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

        } else {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            val params =
                binding.llOffersDeals.layoutParams as CoordinatorLayout.LayoutParams
            params.setMargins(0, 0, 0, 0)
            binding.llOffersDeals.layoutParams = params
        }
    }

    override fun onBackPressed() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            val params =
                binding.llOffersDeals.layoutParams as CoordinatorLayout.LayoutParams
            params.setMargins(0, 0, 0, 0)
            binding.llOffersDeals.layoutParams = params
        } else
            super.onBackPressed()
    }

//    private fun showSnackbarOnSwitchAction(message: String, isSuccess: Boolean) {
//        SimpleCustomSnackbar.showSwitchMsg.make(
//            binding.nested,
//            message,
//            Snackbar.LENGTH_LONG,
//            isSuccess
//        )?.show()
//    }


}
