package com.payu.baas.core.view.ui.activity.passbook

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.payu.baas.core.R
import com.payu.baas.core.app.BaasUIApp
import com.payu.baas.core.databinding.LayoutPassbookTimePeriodBinding
import com.payu.baas.core.model.datasource.PassBookFilterDataSource
import com.payu.baas.core.model.entities.model.TypeModel
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.util.BaaSConstantsUI
import com.payu.baas.core.util.UtilsUI
import com.payu.baas.core.view.viewExtension.CustomEdittext
import java.text.SimpleDateFormat
import java.util.*


class PassBookTimePeriodFilterBottomDialogFragment : BottomSheetDialogFragment(),
    View.OnClickListener {

    companion object {

        fun newInstance(): PassBookTimePeriodFilterBottomDialogFragment =
            PassBookTimePeriodFilterBottomDialogFragment().apply {
                /*
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
                 */
            }

        private const val ACTION_NEW_ALBUM = 1

    }

    private var mAdapter: PassBookFilterTypeSelectionAdapter? = null
    private var binding: LayoutPassbookTimePeriodBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = binding!!

    private lateinit var viewModel: PassBookViewModel
    private val ddmmyyyy = "DDMMYYYY"

    //Transparent Background
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    //Handle BackPress Event
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireActivity(), theme) {
            override fun onBackPressed() {
                //do your stuff
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_passbook_time_period,
            container,
            false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PassBookViewModel::class.java]

        applyThemeToUI()
        setupUI()
    }

    private fun applyThemeToUI() {

        UtilsUI.applyColorToIcon(
            mBinding.ivCancel,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorAsHint(
            mBinding.etStartDate,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToIcon(
            mBinding.ivStartDate,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorAsHint(
            mBinding.etEndDate,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToIcon(
            mBinding.ivEndDate,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvTitle,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvStartDate,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )

        UtilsUI.applyColorToText(
            mBinding.tvEndDate,
            SessionManagerUI.getInstance(requireActivity()).accentColor
        )
    }

    private fun setupUI() {

        //Set previously selected type
        val list = PassBookFilterDataSource.timePeriodTypes
        list.forEach { typeModel ->
            if (viewModel.selectedTimePeriodType?.code == typeModel.code) {
                typeModel.selected = true
                return@forEach
            }
        }

        //Here we display previously selected Start & End date if selected type is 'Custom'
        if (viewModel.selectedTimePeriodType?.code == PassBookFilterDataSource.CUSTOM_TIME_PERIOD) {
            if (viewModel.selectedStartDate.isNotEmpty()) {
                binding?.etStartDate?.setText(viewModel.selectedStartDate)
            }
            if (viewModel.selectedEndDate.isNotEmpty()) {
                binding?.etEndDate?.setText(viewModel.selectedEndDate)
            }
        } else {
            viewModel.selectedStartDate = ""
            viewModel.selectedEndDate = ""
        }

        mBinding.rvTimePeriod.layoutManager = GridLayoutManager(requireActivity(), 2)
        mAdapter =
            PassBookFilterTypeSelectionAdapter(list)
        mBinding.rvTimePeriod.adapter = mAdapter

        mBinding.etStartDate.setText(viewModel.selectedStartDate)
        mBinding.etEndDate.setText(viewModel.selectedEndDate)


        mBinding.llChooseStartDate.setOnClickListener(this)
        mBinding.llChooseEndDate.setOnClickListener(this)
        mBinding.llCancel.setOnClickListener(this)
        mBinding.btnApply.setOnClickListener(this)

        setFromDateEditText(mBinding.etStartDate)
        setToDateEditText(mBinding.etEndDate)

        if (viewModel.selectedStartDate.isEmpty()
            && viewModel.selectedEndDate.isEmpty()
        ) {
            mBinding.etEndDate.isEnabled = false
            mBinding.llChooseEndDate.isEnabled = false
        }

        mBinding.etStartDate.onFocusChangeListener =
            View.OnFocusChangeListener { p0, hasFocus ->
                if (hasFocus) {
                    mAdapter?.markCustomOptionSelected()
                }
            }

        mBinding.etEndDate.onFocusChangeListener =
            View.OnFocusChangeListener { p0, hasFocus ->
                if (hasFocus) {
                    mAdapter?.markCustomOptionSelected()
                }
            }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.llCancel -> {
                dismiss()
            }
            R.id.llChooseStartDate -> {
                mAdapter?.markCustomOptionSelected()
                binding?.etStartDate?.let { openFromDatePicker(it) }
            }
            R.id.llChooseEndDate -> {
                mAdapter?.markCustomOptionSelected()
                binding?.etEndDate?.let { openToDatePicker(it) }
            }
            R.id.btnApply -> {

                //Here we extract currently selected type
                var selectedTypeModel: TypeModel? = null
                mAdapter?.getList()?.let {
                    it.forEach { typeModel ->
                        if (typeModel.selected) {
                            selectedTypeModel = typeModel
                            return@forEach
                        }
                    }
                }

                selectedTypeModel?.let {

                    activity?.let {act->
                        if(act is PassBookActivity) {
                            viewModel.baseCallBack!!.cleverTapUserPassbookEvent(
                                BaaSConstantsUI.CL_PASSBOOK_TIME_FILTER_TXNS,
                                BaaSConstantsUI.CL_PASSBOOK_TIME_FILTER_TXNS_EVENT_ID,
                                SessionManager.getInstance(requireActivity()).accessToken,
                                (act as PassBookActivity).imei,
                                (act as PassBookActivity).mobileNumber,
                                Date(),
                                it.label,
                                "",
                                "",
                                "",
                                "",
                                ""
                            )
                        }
                    }


                    if (it.label == PassBookFilterDataSource.CUSTOM_TIME_PERIOD) {
                        val strStartDate = binding?.etStartDate?.text.toString().trim()
                        val strEndDate = binding?.etEndDate?.text.toString().trim()
                        if (strStartDate.isEmpty()) {
                            BaasUIApp.ctx?.let { act ->
                                UtilsUI.showSnackbarOnSwitchAction(
                                    mBinding.root,
                                    String.format(
                                        getString(R.string.msg_choose_date),
                                        getString(R.string.label_start_date)
                                    ),
                                    false
                                )
                            }
                        } else if (!checkDate(strStartDate)
                        ) {
                            BaasUIApp.ctx?.let { act ->
                                UtilsUI.showSnackbarOnSwitchAction(
                                    mBinding.root,
                                    String.format(
                                        getString(R.string.msg_enter_valid_date),
                                        getString(R.string.label_start_date)
                                    ),
                                    false
                                )
                            }
                        } else if (strEndDate.isEmpty()) {
                            BaasUIApp.ctx?.let { act ->
                                UtilsUI.showSnackbarOnSwitchAction(
                                    mBinding.root,
                                    String.format(
                                        getString(R.string.msg_choose_date),
                                        getString(R.string.label_end_date)
                                    ),
                                    false
                                )
                            }
                        } else if (!checkDate(strEndDate)
                        ) {
                            BaasUIApp.ctx?.let { act ->
                                UtilsUI.showSnackbarOnSwitchAction(
                                    mBinding.root,
                                    String.format(
                                        getString(R.string.msg_enter_valid_date),
                                        getString(R.string.label_end_date)
                                    ),
                                    false
                                )
                            }
                        } else {

                            val startDate = SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).parse(strStartDate)
                            val endDate = SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).parse(strEndDate)

                            if (startDate.after(Date())
                                || endDate.after(Date())) {

                                BaasUIApp.ctx?.let { act ->
                                    UtilsUI.showSnackbarOnSwitchAction(
                                        mBinding.root,
                                        getString(R.string.msg_future_date_not_allowed),
                                        false
                                    )
                                }
                            } else if (startDate.after(endDate)) {

                                BaasUIApp.ctx?.let { act ->
                                    UtilsUI.showSnackbarOnSwitchAction(
                                        mBinding.root,
                                        String.format(
                                            getString(R.string.msg_end_date_must_be_greater_than_start_date),
                                            getString(R.string.label_end_date),
                                            getString(R.string.label_start_date)
                                        ),
                                        false
                                    )
                                }
                            } else {
                                viewModel.selectedTimePeriodType = it
                                viewModel.selectedStartDate = strStartDate
                                viewModel.selectedEndDate = strEndDate
                                viewModel.applyTimePeriodSelectionFilter()
                                dismiss()
                            }
                        }
                    } else {
                        viewModel.selectedTimePeriodType = it
                        viewModel.selectedStartDate = ""
                        viewModel.selectedEndDate = ""
                        viewModel.applyTimePeriodSelectionFilter()
                        dismiss()
                    }
                }

            }
        }
    }

    private fun setFromDateEditText(et: CustomEdittext) {

        et.addTextChangedListener(object : TextWatcher {

            private val cal = Calendar.getInstance()

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                var working: String = p0.toString()
                var isValid = false
                try {

                    if (working.length == 2 && p2 == 0) {
                        if (working.toInt() < 1 || working.toInt() > 31) {
                            //isValid = false
                            var str = working.removeRange(0, 2)
                            if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                                str = str.plus("0").plus(cal.get(Calendar.DAY_OF_MONTH))
                            } else {
                                str = str.plus(cal.get(Calendar.DAY_OF_MONTH))
                            }
                            working = str
                        }
                        working += "/"
                        et.setText(working)
                        et.setSelection(working.length)
                    } else if (working.length == 5 && p2 == 0) {
                        if (working.substring(3, 5).toInt() < 1 || working.substring(3, 5)
                                .toInt() > 12
                        ) {
                            //isValid = false
                            var str = working.removeRange(3, 5)
                            if (cal.get(Calendar.MONTH) + 1 < 10) {
                                str = str.plus("0").plus(cal.get(Calendar.MONTH) + 1)
                            } else {
                                str = str.plus(cal.get(Calendar.MONTH) + 1)
                            }
                            working = str
                        }
                        working += "/"
                        et.setText(working)
                        et.setSelection(working.length)
                    } else if (working.length == 10 && p2 == 0) {

                        if (working.substring(6, 10).toInt() > cal.get(Calendar.YEAR)
                        ) {
                            //isValid = false
                            var str = working.removeRange(6, 10)
                            str = str.plus(cal.get(Calendar.YEAR))
                            working = str
                        }
                        et.setText(working)
                        et.setSelection(working.length)
                        isValid = true
                    }

                    /*if(working.contains("/")){
                        var str=""
                        val strDay=working.split("/")[0]
                        if(strDay.isNotEmpty()){
                            if(strDay.length==1){
                                str = "0".plus(strDay)
                            }
                            working=str
                        }
                        working += "/"
                        et.setText(working)
                        et.setSelection(working.length)
                    }*/
                } catch (e: Exception) {
                }

                if (isValid) {
                    mBinding.etEndDate.isEnabled = true
                    mBinding.llChooseEndDate.isEnabled = true
                } else {
                    mBinding.etEndDate.text.clear()
                    mBinding.etEndDate.isEnabled = false
                    mBinding.llChooseEndDate.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable) {

            }
        })
    }

    private fun setToDateEditText(et: CustomEdittext) {

        et.addTextChangedListener(object : TextWatcher {

            private val cal = Calendar.getInstance()

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var working: String = p0.toString()
                try {

                    if (working.length == 2 && p2 == 0) {
                        if (working.toInt() < 1 || working.toInt() > 31) {
                            //isValid = false
                            var str = working.removeRange(0, 2)
                            if (cal.get(Calendar.DAY_OF_MONTH) < 10) {
                                str = str.plus("0").plus(cal.get(Calendar.DAY_OF_MONTH))
                            } else {
                                str = str.plus(cal.get(Calendar.DAY_OF_MONTH))
                            }
                            working = str
                        }
                        working += "/"
                        et.setText(working)
                        et.setSelection(working.length)
                    } else if (working.length == 5 && p2 == 0) {
                        if (working.substring(3, 5).toInt() < 1 || working.substring(3, 5)
                                .toInt() > 12
                        ) {
                            //isValid = false
                            var str = working.removeRange(3, 5)
                            if (cal.get(Calendar.MONTH) + 1 < 10) {
                                str = str.plus("0").plus(cal.get(Calendar.MONTH) + 1)
                            } else {
                                str = str.plus(cal.get(Calendar.MONTH) + 1)
                            }
                            working = str
                        }
                        working += "/"
                        et.setText(working)
                        et.setSelection(working.length)
                    } else if (working.length == 10 && p2 == 0) {

                        if (working.substring(6, 10).toInt() > cal.get(Calendar.YEAR)
                        ) {
                            //isValid = false
                            var str = working.removeRange(6, 10)
                            str = str.plus(cal.get(Calendar.YEAR))
                            working = str
                        }
                        et.setText(working)
                        et.setSelection(working.length)
                    }

                    /*if(working.contains("/")){
                        var str=""
                        val strDay=working.split("/")[0]
                        if(strDay.isNotEmpty()){
                            if(strDay.length==1){
                                str = "0".plus(strDay)
                            }
                            working=str
                        }
                        working += "/"
                        et.setText(working)
                        et.setSelection(working.length)
                    }*/
                } catch (e: Exception) {
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable) {

            }
        })
    }


    private fun openFromDatePicker(editText: EditText) {

        val strDate = editText.text.toString().trim()

        var date: Date? = null
        if (strDate.isNotEmpty()
            && checkDate(strDate)
        ) {
            date = SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).parse(strDate)
        }

        val cal = Calendar.getInstance()
        date?.let {
            cal.time = it
        }

        val tempYear = cal.get(Calendar.YEAR)
        val tempMonth = cal.get(Calendar.MONTH)
        val tempDay = cal.get(Calendar.DAY_OF_MONTH)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                editText.setText(SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).format(cal.time))

                mBinding.etEndDate.text.clear()
                mBinding.etEndDate.isEnabled = true
                mBinding.llChooseEndDate.isEnabled = true
            }
        val dialog = DatePickerDialog(
            requireActivity(), R.style.datepicker,
            dateSetListener,
            // setting DatePickerDialog to point to today's date when it loads up
            tempYear,
            tempMonth,
            tempDay
        )
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }


    private fun openToDatePicker(editText: EditText) {

        val strStartDate = mBinding.etStartDate.text.toString().trim()
        if (strStartDate.isEmpty()) {
            UtilsUI.showSnackbarOnSwitchAction(
                mBinding.root,
                String.format(
                    getString(R.string.msg_choose_date),
                    getString(R.string.label_start_date)
                ),
                false
            )
            return
        }

        if (!checkDate(strStartDate)) {
            UtilsUI.showSnackbarOnSwitchAction(
                mBinding.root,
                String.format(
                    getString(R.string.msg_enter_valid_date),
                    getString(R.string.label_start_date)
                ),
                false
            )
            return
        }


        val startDate: Date? = SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).parse(strStartDate)
        val calStartDate = Calendar.getInstance()
        startDate?.let {
            calStartDate.time = it
        }


        val strEndDate = editText.text.toString().trim()
        var endDate: Date? = null
        if (strEndDate.isNotEmpty()
            && checkDate(strEndDate)
        ) {
            endDate = SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).parse(strEndDate)
        }
        val cal = Calendar.getInstance()
        endDate?.let {
            cal.time = it
        }
        val tempYear = cal.get(Calendar.YEAR)
        val tempMonth = cal.get(Calendar.MONTH)
        val tempDay = cal.get(Calendar.DAY_OF_MONTH)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                editText.setText(SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault()).format(cal.time))
            }
        val dialog = DatePickerDialog(
            requireActivity(), R.style.datepicker,
            dateSetListener,
            // setting DatePickerDialog to point to today's date when it loads up
            tempYear,
            tempMonth,
            tempDay
        )
        dialog.datePicker.minDate = calStartDate.timeInMillis
        dialog.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dialog.show()
    }

    fun checkDate(date: String): Boolean {
        val pattern = Regex("(0?[1-9]|[12][0-9]|3[01])\\/(0?[1-9]|1[0-2])\\/([0-9]{4})")
        var flag = false
        if (date.matches(pattern)) {
            flag = true
        }
        return flag
    }

    override fun onDestroyView() {
        mAdapter?.clearAdapter()
        super.onDestroyView()
    }
}