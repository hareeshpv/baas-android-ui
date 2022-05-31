package com.payu.baas.core.view.ui.activity.cards

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ActivityCardsBinding
import com.payu.baas.core.enums.ApiName
import com.payu.baas.core.model.ErrorResponse
import com.payu.baas.core.model.ErrorResponseUI
import com.payu.baas.core.model.responseModels.*
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.JsonUtil
import com.payu.baas.core.util.Resource
import com.payu.baas.core.util.Status
import com.payu.baas.core.view.ui.BaseActivity
import com.payu.baas.core.view.ui.activity.cardBlockReason.CardBlockReasonActivity
import com.payu.baas.core.view.ui.activity.cardUpdatePin.UpadatePinActivity
import com.payu.baas.core.view.ui.snackbaar.SimpleCustomSnackbar
import java.util.*

class CardsActivity : BaseActivity() {
    lateinit var binding: ActivityCardsBinding
    private lateinit var viewModel: CardsViewModel
    var CARD_STATUS_ACTIVE = "ACTIVE"
    var CARD_STATUS_INACTIVE = "INACTIVE"
    var isCardDelivered: Boolean = false
    var isCardActive: String = CARD_STATUS_INACTIVE

    //    var CARD_DELIVERED = "1"

    var PIN_UPDATED_STATUS = "1"
    lateinit var front_anim: AnimatorSet
    lateinit var back_anim: AnimatorSet
    var isFront = true

    //    var lastCallFrom = LastCall.NONE
    var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cards)
        activity = this
        setupViewModel()
        setupObserver()
        setupUI()
    }

    @SuppressLint("ResourceType")
    private fun setupUI() {
        mobileNumber = SessionManagerUI.getInstance(applicationContext).userMobileNumber
        viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_CARD_MANAGEMENT_PAGE_LOAD,
            BaaSConstantsUI.CL_USER_CARD_MANAGEMENT_PAGE_LOAD_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        initialCardSettings()
        var scale = applicationContext.resources.displayMetrics.density
        binding.llCardFront.cameraDistance = 8000 * scale
        binding.llCardBack.cameraDistance = 8000 * scale


        // Now we will set the front animation
        front_anim =
            AnimatorInflater.loadAnimator(applicationContext, R.anim.front_animator) as AnimatorSet
        back_anim =
            AnimatorInflater.loadAnimator(applicationContext, R.anim.back_animator) as AnimatorSet
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            CardsViewModel.CardViewModelFactory(this, this)
        )[CardsViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.imeiNumber = imei

    }

    private fun setupObserver() {
        viewModel.getCardDetailsResponseObs().observe(this, {
            parseResponse(it, ApiName.CARD_DETAILS)
        })
        viewModel.getCardImageResponseObs().observe(this, {
            parseResponse(it, ApiName.CARD_IMAGE)
        })
        viewModel.getCardPinStatusResponseObs().observe(this, {
            parseResponse(it, ApiName.GET_PIN_STATUS)
        })
        viewModel.getSTransactionModeResponseObs().observe(this, {
            parseResponse(it, ApiName.SET_TRANSACTION_MODE)
        })
        viewModel.getCardReOrderObs().observe(this, {
            parseResponse(it, ApiName.CARD_REORDER)
        })
        viewModel.getBlockResponseObs().observe(this, {
            parseResponse(it, ApiName.BLOCK_CARD)
        })
        viewModel.getUnBlockResponseObs().observe(this, {
            parseResponse(it, ApiName.UNBLOCK_CARD)
        })
    }

    private fun parseResponse(it: Resource<Any>?, apiName: ApiName) {
        when (it?.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiName) {
                    ApiName.CARD_DETAILS -> {
                        it.data?.let {
                            if (it is CardDetailResponse) {
                                setCardDetails(it)
                            }
                        }
                    }
                    ApiName.CARD_IMAGE -> {
                        it.data?.let {
                            if (it is CardImageResponse) {
                                if (!(it.cardImageUrl.isNullOrEmpty())) {
                                    setCardImage(it.cardImageUrl!!)
                                }
                            }
                        }
                    }
                    ApiName.GET_PIN_STATUS -> {
                        it.data?.let {
                            if (it is GetCardPinStatusResponse) {
                                if (it.status.equals(PIN_UPDATED_STATUS))
                                    showSnackbarOnSwitchAction(
                                        BaaSConstantsUI.CARD_PIN_CHANGED_SUCCESS_MSG,
                                        true
                                    )
                                else
                                    showSnackbarOnSwitchAction(
                                        BaaSConstantsUI.CARD_PIN_CHANGED_FAILURE_MSG,
                                        false
                                    )
                                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                                    BaaSConstantsUI.CL_USER_CHANGE_CARD_PIN,
                                    BaaSConstantsUI.CL_USER_CHANGE_CARD_PIN_EVENT_ID,
                                    SessionManager.getInstance(this).accessToken,
                                    imei,
                                    mobileNumber,
                                    Date(),
                                    it.status!!,
                                    ""
                                )
                            }
                        }
                    }
                    ApiName.CARD_REORDER -> {
                        it.data?.let {
                            if (it is CardReorderResponse) {
                                it.message?.let { it1 -> showSnackbarOnSwitchAction(it1, true) }
                                viewModel.getCardDetails()
                            }
                        }
                    }
                    ApiName.UNBLOCK_CARD -> {
                        it.data?.let {
                            if (it is UnblockCardResponse) {
                                SessionManagerUI.getInstance(this).isCardBlocked = it.status
                                showSnackbarOnSwitchAction(
                                    BaaSConstantsUI.CARD_UNBLOCKED_SUCCESS_MSG,
                                    true
                                )
                                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                                    BaaSConstantsUI.CL_USER_CHANGE_CARD_STATUS,
                                    BaaSConstantsUI.CL_USER_CHANGE_CARD_STATUS_EVENT_ID,
                                    SessionManager.getInstance(this).accessToken,
                                    imei,
                                    mobileNumber,
                                    Date(),
                                    it.status!!,
                                    ""
                                )
                                viewModel.getCardDetails()
                            }
                        }
                    }
                    ApiName.SET_TRANSACTION_MODE -> {
                        hideProgress()
                        it.data?.let {
                            if (it is SetTransactionModeResponse) {
                                setTransactionModeResponse()
                            }
                        }
                    }
                    ApiName.BLOCK_CARD -> {
                        it.data?.let {
                            if (it is BlockCardResponse) {
                                SessionManagerUI.getInstance(this).isCardBlocked = it.status
                                showSnackbarOnSwitchAction(
                                    BaaSConstantsUI.CARD_BLOCKED_SUCCESS_MSG,
                                    true
                                )
                                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                                    BaaSConstantsUI.CL_USER_CHANGE_CARD_STATUS,
                                    BaaSConstantsUI.CL_USER_CHANGE_CARD_STATUS_EVENT_ID,
                                    SessionManager.getInstance(this).accessToken,
                                    imei,
                                    mobileNumber,
                                    Date(),
                                    it.status!!,
                                    ""
                                )
                                viewModel.getCardDetails()
                            }
                        }
                    }
                }

            }
            Status.LOADING -> {
                showProgress("")
            }
            Status.ERROR -> {
                //Handle Error
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
                    viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                        BaaSConstantsUI.CL_USER_CARD_MANAGEMENT_PAGE_ERROR,
                        BaaSConstantsUI.CL_USER_CARD_MANAGEMENT_PAGE_ERROR_EVENT_ID,
                        SessionManager.getInstance(this).accessToken,
                        imei,
                        mobileNumber,
                        Date(),
                        "",
                        ""
                    )
                }
            }
            Status.RETRY -> {
                hideProgress()
                when (apiName) {
                    ApiName.CARD_DETAILS -> {
                        viewModel.getCardDetails()
                    }
                    ApiName.CARD_IMAGE -> {
                        viewModel.getCardBackImage()
                    }
                    ApiName.GET_PIN_STATUS -> {
                        viewModel.getCardPinStatus()
                    }
//                    ApiName.GET_CLIENT_TOKEN -> {
//                        viewModel.reGenerateAccessToken(false)
//                    }
//                    ApiName.UNBLOCK_CARD -> {
//                        viewModel.unBlockCard()
//                    }
//                    ApiName.GET_TRANSACTION_MODE -> {
//                        viewModel.getTransactionMode()
//                    }
                    ApiName.SET_TRANSACTION_MODE -> {
                        viewModel.setTransactionMode()
                    }
                }
            }
        }
    }

    fun initialCardSettings() {
        binding.sCardActiveStatus.isChecked = false
        binding.tvCardActiveStatus.setTextColor(
            ContextCompat.getColor(
                binding.tvCardActiveStatus.context,
                R.color.subtext_color
            )
        )
        binding.sOnlineTransaction.isChecked = false
        binding.tvOnlineTransactions.setTextColor(
            ContextCompat.getColor(
                binding.tvOnlineTransactions.context,
                R.color.subtext_color
            )
        )
        binding.sAtmCashWithdrawal.isChecked = false
        binding.tvAtmCashWithdrawal.setTextColor(
            ContextCompat.getColor(
                binding.tvAtmCashWithdrawal.context,
                R.color.subtext_color
            )
        )
        binding.sCardSwipe.isChecked = false
        binding.tvCardSwipe.setTextColor(
            ContextCompat.getColor(
                binding.tvCardSwipe.context,
                R.color.subtext_color
            )
        )
        binding.tvCardBlock.setTextColor(
            ContextCompat.getColor(
                binding.tvCardBlock.context,
                R.color.subtext_color
            )
        )
        binding.tvChangePin.setTextColor(
            ContextCompat.getColor(
                binding.tvChangePin.context,
                R.color.subtext_color
            )
        )
    }

    fun setCardDetails(cardDetails: CardDetailResponse) {
        SessionManagerUI.getInstance(this).cardActiveStatus = cardDetails.status
        isCardDelivered = cardDetails.cardReceived!!
        isCardActive = cardDetails.status!!
//        SessionManagerUI.getInstance(this).isCardDelivered = cardDetails.cardReceived
        if (isCardActive.equals(CARD_STATUS_ACTIVE, false) == true && isCardDelivered) {
            binding.sOnlineTransaction.isClickable = true
            binding.sCardSwipe.isClickable = true
            binding.sAtmCashWithdrawal.isClickable = true
        } else {
            if (isCardActive.equals(CARD_STATUS_ACTIVE, false) == true)
                binding.sOnlineTransaction.isClickable = true
            else
                binding.sOnlineTransaction.isClickable = false
            binding.sCardSwipe.isClickable = false
            binding.sAtmCashWithdrawal.isClickable = false
        }
        if (isCardActive.equals(CARD_STATUS_ACTIVE, false) == true) {
            binding.sCardActiveStatus.isChecked = true
            binding.tvCardActiveStatus.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardActiveStatus.context,
                    R.color.text_color
                )
            )

        } else {
            binding.sCardActiveStatus.isChecked = false
            binding.tvCardActiveStatus.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardActiveStatus.context,
                    R.color.subtext_color
                )
            )

        }
        if (cardDetails.allowOnlineTransaction == true) {
            binding.sOnlineTransaction.isChecked = true
            if (isCardActive.equals(CARD_STATUS_ACTIVE, false) == true)
            binding.tvOnlineTransactions.setTextColor(
                ContextCompat.getColor(
                    binding.tvOnlineTransactions.context,
                    R.color.text_color
                )
            )
            else
                binding.tvOnlineTransactions.setTextColor(
                    ContextCompat.getColor(
                        binding.tvOnlineTransactions.context,
                        R.color.subtext_color
                    )
                )
        } else {
            binding.sOnlineTransaction.isChecked = false
            binding.tvOnlineTransactions.setTextColor(
                ContextCompat.getColor(
                    binding.tvOnlineTransactions.context,
                    R.color.subtext_color
                )
            )
        }
        if (cardDetails.allowAtmTransaction == true) {
            binding.sAtmCashWithdrawal.isChecked = true
            if (isCardActive.equals(CARD_STATUS_ACTIVE, false) == true)
            binding.tvAtmCashWithdrawal.setTextColor(
                ContextCompat.getColor(
                    binding.tvAtmCashWithdrawal.context,
                    R.color.text_color
                )
            )
            else
                binding.tvAtmCashWithdrawal.setTextColor(
                    ContextCompat.getColor(
                        binding.tvAtmCashWithdrawal.context,
                        R.color.subtext_color
                    )
                )
        } else {
            binding.sAtmCashWithdrawal.isChecked = false
            binding.tvAtmCashWithdrawal.setTextColor(
                ContextCompat.getColor(
                    binding.tvAtmCashWithdrawal.context,
                    R.color.subtext_color
                )
            )
        }
        if (cardDetails.allowCardSwipe == true) {
            binding.sCardSwipe.isChecked = true
            if (isCardActive.equals(CARD_STATUS_ACTIVE, false) == true)
            binding.tvCardSwipe.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardSwipe.context,
                    R.color.text_color
                )
            )
            else
                binding.tvCardSwipe.setTextColor(
                    ContextCompat.getColor(
                        binding.tvCardSwipe.context,
                        R.color.subtext_color
                    )
                )
        } else {
            binding.sCardSwipe.isChecked = false
            binding.tvCardSwipe.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardSwipe.context,
                    R.color.subtext_color
                )
            )
        }
        if (isCardDelivered == true) {
            binding.tvCardBlock.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardBlock.context,
                    R.color.text_color
                )
            )
            binding.tvChangePin.setTextColor(
                ContextCompat.getColor(
                    binding.tvChangePin.context,
                    R.color.text_color
                )
            )

        } else {
            binding.tvCardBlock.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardBlock.context,
                    R.color.subtext_color
                )
            )
            binding.tvChangePin.setTextColor(
                ContextCompat.getColor(
                    binding.tvChangePin.context,
                    R.color.subtext_color
                )
            )
        }
//        var cardImage = com.payu.baas.core.storage.SessionManager.getInstance(this).cardImage
//        if (cardImage.isNullOrEmpty()) {
            viewModel.getCardBackImage()
//        } else {
//            setCardImage(cardImage)
//        }
    }

    fun setCardImage(cardImage: String) {
//        var decodedStringImage: String =
//            Utils.decodeString(
//                cardImage
//            )
//        val photoUrl = URL(decodedStringImage)
//        var mIcon = BitmapFactory.decodeStream(
//            photoUrl.openConnection().getInputStream()
//        )
//        binding.ivCardBack.setImageBitmap(mIcon)
        var cardImage =
            com.payu.baas.core.storage.SessionManager.getInstance(this).cardImage
        Glide.with(applicationContext)
            .load(cardImage)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivCardBack)

//        binding.ivCardBack.setColorFilter(ContextCompat.getColor(
//            binding.ivCardBack.context,
//            R.color.black
//        ))
    }

    fun openPreviousScreen(view: android.view.View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
            BaaSConstantsUI.CL_USER_CARD_MANAGEMENT_GO_BACK_HOME,
            BaaSConstantsUI.CL_USER_CARD_MANAGEMENT_GO_BACK_HOME_EVENT_ID,
            SessionManager.getInstance(this).accessToken,
            imei,
            mobileNumber,
            Date(),
            "",
            ""
        )
        finish()
    }

    fun clickActions(view: View) {
        when (view) {
            binding.sAtmCashWithdrawal -> {
                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_TOGGLE_ATM_WITHDRAWAL,
                    BaaSConstantsUI.CL_USER_TOGGLE_ATM_WITHDRAWAL_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    binding.sAtmCashWithdrawal.isChecked.toString(),
                    ""
                )
//                if (isCardDelivered && isCardActive.equals(CARD_STATUS_ACTIVE, false)==true) {
                viewModel.tModeChannel = BaaSConstantsUI.BS_VALUE_ATM_CHANNEL
                viewModel.tModeAllow = binding.sAtmCashWithdrawal.isChecked
                viewModel.setTransactionMode()
//                }
//                else {
//                    if (isCardActive.equals(CARD_STATUS_ACTIVE, false)==false)
//                        showSnackbarOnSwitchAction(
//                            BaaSConstantsUI.CARD_INACTIVE_FOR_PAYMENT_MODES_MSG,
//                            false
//                        )
//                    else
//                    showSnackbarOnSwitchAction(BaaSConstantsUI.CARD_NOT_DELIVERED_INACTIVE_MSG, false)
////                    binding.sAtmCashWithdrawal.isChecked = false
//                    binding.tvAtmCashWithdrawal.setTextColor(
//                        ContextCompat.getColor(
//                            binding.tvAtmCashWithdrawal.context,
//                            R.color.subtext_color
//                        )
//                    )
//                }
            }
            binding.sCardActiveStatus -> {
                if (binding.sCardActiveStatus.isChecked)
                    viewModel.unBlockCard()
//                    setModesAsPercardStatus(CARD_STATUS_ACTIVE)
                else
                    viewModel.blockCard()
//                    setModesAsPercardStatus(CARD_STATUS_INACTIVE)
                //willcall this when api is available
//                viewModel.setCardStatus()
            }
            binding.sCardSwipe -> {
                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_TOGGLE_CARD_SWIPE,
                    BaaSConstantsUI.CL_USER_TOGGLE_CARD_SWIPE_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    binding.sCardSwipe.isChecked.toString(),
                    ""
                )
//                if (isCardDelivered && isCardActive.equals(CARD_STATUS_ACTIVE, false)==true) {
                viewModel.tModeChannel = BaaSConstantsUI.BS_VALUE_SWIPE_CHANNEL
                viewModel.tModeAllow = binding.sCardSwipe.isChecked
                viewModel.setTransactionMode()
//                }
//                else {
//                    binding.sCardSwipe.isChecked = false
//                    if (isCardActive.equals(CARD_STATUS_ACTIVE, false)==false)
//                        showSnackbarOnSwitchAction(
//                            BaaSConstantsUI.CARD_INACTIVE_FOR_PAYMENT_MODES_MSG,
//                            false
//                        )
//                    else
//                    showSnackbarOnSwitchAction(BaaSConstantsUI.CARD_NOT_DELIVERED_INACTIVE_MSG, false)
//
//                    binding.tvCardSwipe.setTextColor(
//                        ContextCompat.getColor(
//                            binding.tvCardSwipe.context,
//                            R.color.subtext_color
//                        )
//                    )
//                }
            }
            binding.sOnlineTransaction -> {
                viewModel.baseCallBack!!.cleverTapUserCardManagementEvent(
                    BaaSConstantsUI.CL_USER_TOGGLE_ONLINE_TRANSACTION,
                    BaaSConstantsUI.CL_USER_TOGGLE_ONLINE_TRANSACTION_EVENT_ID,
                    SessionManager.getInstance(this).accessToken,
                    imei,
                    mobileNumber,
                    Date(),
                    binding.sOnlineTransaction.isChecked.toString(),
                    ""
                )
//                if (isCardActive.equals(CARD_STATUS_ACTIVE, false)==true){
                viewModel.tModeAllow = binding.sOnlineTransaction.isChecked
                viewModel.tModeChannel = BaaSConstantsUI.BS_VALUE_ONLINE_CHANNEL
                viewModel.setTransactionMode()
//                } else {
//                    binding.sOnlineTransaction.isChecked = false
//                    showSnackbarOnSwitchAction(
//                        BaaSConstantsUI.CARD_INACTIVE_FOR_PAYMENT_MODES_MSG,
//                        false
//                    )
//                    binding.tvOnlineTransactions.setTextColor(
//                        ContextCompat.getColor(
//                            binding.tvOnlineTransactions.context,
//                            R.color.subtext_color
//                        )
//                    )
//                }
            }
            binding.rlCard -> {
                if (isFront) {
                    front_anim.setTarget(binding.llCardFront);
                    back_anim.setTarget(binding.llCardBack);
//                binding.ivCardBack.visibility = View.VISIBLE
                    front_anim.start()
                    back_anim.start()
                    isFront = false
                } else {
                    front_anim.setTarget(binding.llCardBack)
                    back_anim.setTarget(binding.llCardFront)
                    back_anim.start()
                    front_anim.start()
                    isFront = true
                }
            }
        }
    }

    fun setModesAsPercardStatus(status: String) {
        if (status.equals(CARD_STATUS_ACTIVE, true)) {
//            SessionManagerUI.getInstance(this).cardActiveStatus = CARD_STATUS_ACTIVE
            showSnackbarOnSwitchAction(BaaSConstantsUI.CARD_ACTIVE_STATUS_MSG, true)
            binding.tvCardActiveStatus.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardActiveStatus.context,
                    R.color.text_color
                )
            )
        } else {
//            SessionManagerUI.getInstance(this).cardActiveStatus = CARD_STATUS_INACTIVE
            showSnackbarOnSwitchAction(BaaSConstantsUI.CARD_INACTIVE_STATUS_MSG, false)
            binding.tvCardActiveStatus.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardActiveStatus.context,
                    R.color.subtext_color
                )
            )
            // will uncomment this code onapi call
            /*binding.sAtmCashWithdrawal.isChecked = false
            binding.tvAtmCashWithdrawal.setTextColor(
                ContextCompat.getColor(
                    binding.tvAtmCashWithdrawal.context,
                    R.color.subtext_color
                )
            )
            binding.sCardSwipe.isChecked = false
            binding.tvCardSwipe.setTextColor(
                ContextCompat.getColor(
                    binding.tvCardSwipe.context,
                    R.color.subtext_color
                )
            )*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ModuleCall.BLOCK.ordinal) {
                var isCardBlocked = SessionManagerUI.getInstance(this).isCardBlocked
                if (isCardBlocked.equals("INACTIVE")) {
                    com.payu.baas.core.storage.SessionManager.getInstance(this).cardImage = ""
                    showSnackbarOnSwitchAction(BaaSConstantsUI.CARD_BLOCKED_SUCCESS_MSG, true)
                    viewModel.cardReOrder()
                } else {
                    showSnackbarOnSwitchAction(BaaSConstantsUI.CARD_BLOCKED_FAILURE_MSG, false)
                }
            } else if (requestCode == ModuleCall.SET_LIMIT.ordinal) {
                showSnackbarOnSwitchAction(
                    BaaSConstantsUI.TRANSACTION_LIMIT_CHANGED_SUCCESS_MSG,
                    true
                )
            } else if (requestCode == ModuleCall.CHANGE_PIN.ordinal) {
                viewModel.getCardPinStatus()
            }
        }
    }

    fun goToCardBlockReasonScreen(view: View) {
        if (isCardDelivered) {
            val bundle = Bundle()
            bundle.putString(
                BaaSConstantsUI.ARGUMENTS_FROM_MODULE,
                BaaSConstantsUI.BS_KEY_CARD_SCREEN
            )
            val intent = Intent(this, CardBlockReasonActivity::class.java)
            intent.putExtras(bundle)
            startActivityForResult(intent, ModuleCall.BLOCK.ordinal)
        } else {
            showSnackbarOnSwitchAction(BaaSConstantsUI.CARD_NOT_DELIVERED_FOR_BLOCK_MSG, false)
        }
    }

    fun goToChangePin(view: View) {
        if (isCardDelivered) {
            val intent = Intent(this, UpadatePinActivity::class.java)
            startActivityForResult(intent, ModuleCall.CHANGE_PIN.ordinal)
        } else {
            showSnackbarOnSwitchAction(BaaSConstantsUI.CARD_NOT_DELIVERED_FOR_CHANGE_PIN_MSG, false)
        }
    }

//    fun showUnblockDialog() {
//        val builder = AlertDialog.Builder(this)
//        builder.setMessage(
//            "Your card is blocked. Do you want to unblock?"
//        ).setCancelable(false)
//            .setPositiveButton(
//                "Yes"
//            ) { dialog, id ->
//                //to perform on yes
//                viewModel.unBlockCard()
//            }
//            .setNegativeButton(
//                "No"
//            ) { dialog, id ->
//                dialog.cancel();
//            }
//        val alert = builder.create()
//        alert.show()
//    }

    fun setTransactionModeResponse() {
        var msg = ""
        when (viewModel.tModeChannel) {
            BaaSConstantsUI.BS_VALUE_ATM_CHANNEL -> {
                if (viewModel.tModeAllow) {
//                    binding.sAtmCashWithdrawal.isChecked = true
                    msg = BaaSConstantsUI.ATM_ACTIVE_MSG
                    binding.tvAtmCashWithdrawal.setTextColor(
                        ContextCompat.getColor(
                            binding.tvAtmCashWithdrawal.context,
                            R.color.text_color
                        )
                    )
                } else {
//                    binding.sAtmCashWithdrawal.isChecked = false
                    msg = BaaSConstantsUI.ATM_INACTIVE_MSG
                    binding.tvAtmCashWithdrawal.setTextColor(
                        ContextCompat.getColor(
                            binding.tvAtmCashWithdrawal.context,
                            R.color.subtext_color
                        )
                    )
                }
            }
            BaaSConstantsUI.BS_VALUE_SWIPE_CHANNEL -> {
                if (viewModel.tModeAllow) {
//                    binding.sCardSwipe.isChecked = true
                    msg = BaaSConstantsUI.SWIPE_ACTIVE_MSG
                    binding.tvCardSwipe.setTextColor(
                        ContextCompat.getColor(
                            binding.tvCardSwipe.context,
                            R.color.text_color
                        )
                    )
                } else {
//                    binding.sCardSwipe.isChecked = false
                    msg = BaaSConstantsUI.SWIPE_INACTIVE_MSG
                    binding.tvCardSwipe.setTextColor(
                        ContextCompat.getColor(
                            binding.tvCardSwipe.context,
                            R.color.subtext_color
                        )
                    )
                }
            }
            BaaSConstantsUI.BS_VALUE_ONLINE_CHANNEL -> {
                if (viewModel.tModeAllow) {
                    msg = BaaSConstantsUI.ONLINE_TRANSACTION_ACTIVE_MSG
                    binding.tvOnlineTransactions.setTextColor(
                        ContextCompat.getColor(
                            binding.tvOnlineTransactions.context,
                            R.color.text_color
                        )
                    )
                } else {
                    msg = BaaSConstantsUI.ONLINE_TRANSACTION_INACTIVE_MSG
                    binding.tvOnlineTransactions.setTextColor(
                        ContextCompat.getColor(
                            binding.tvOnlineTransactions.context,
                            R.color.subtext_color
                        )
                    )
                }
            }
        }
        showSnackbarOnSwitchAction(msg, viewModel.tModeAllow)
//        SimpleCustomSnackbar.showSwitchMsg.make(
//            binding.nested,
//            msg,
//            Snackbar.LENGTH_LONG,
//            viewModel.tModeAllow
//        )?.show()
    }


    ///
    enum class ModuleCall {
        BLOCK,
        SET_LIMIT,
        CHANGE_PIN,
        NONE
    }

    fun showSnackbarOnSwitchAction(message: String, isSuccess: Boolean) {
        SimpleCustomSnackbar.showSwitchMsg.make(
            binding.nested,
            message,
            Snackbar.LENGTH_LONG,
            isSuccess
        )?.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCardDetails()
    }
}