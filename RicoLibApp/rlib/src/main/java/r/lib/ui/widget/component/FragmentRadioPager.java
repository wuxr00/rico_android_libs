package r.lib.ui.widget.component;

import android.support.v4.view.ViewPager;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * 管理RadioGroup和ViewPager协同显示fragment的界面管理器
 * Created by Rico on 15/9/11.
 */
public class FragmentRadioPager implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {
    private ViewPager viewPager;
    private ViewPager.OnPageChangeListener onViewPageChangedListener;
    private RadioGroup radioGroup;
    private boolean passDeal;
    private int lastPosition;
    private SparseIntArray viewPositionArr = new SparseIntArray();
    private SparseIntArray positionViewArr = new SparseIntArray();

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    public FragmentRadioPager(ViewPager viewPager, final RadioGroup radioGroup) {
        this.viewPager = viewPager;
        this.radioGroup = radioGroup;
        int rbPosition = 0;
        int rbSize = radioGroup.getChildCount();
        for (int i = 0; i < rbSize; i++) {
            View child = radioGroup.getChildAt(i);
            if (child instanceof RadioButton) {
                positionViewArr.put(rbPosition, i);
                viewPositionArr.put(child.getId(), rbPosition);
                rbPosition++;
            }
        }

        onViewPageChangedListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (passDeal) {
                    passDeal = false;
                    return;
                }
                passDeal = true;
                ((RadioButton) radioGroup.getChildAt(positionViewArr.get(position))).setChecked(true);
//                checkNextRadioButton(position);
//                lastPosition = position;
                passDeal = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPager.addOnPageChangeListener(onViewPageChangedListener);
        radioGroup.setOnCheckedChangeListener(this);
//        passDeal = true;
        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
    }

    private void checkNextRadioButton(int position) {
        if (position >= 0 && position < radioGroup.getChildCount()) {
            View child = radioGroup.getChildAt(position);
            if (!(child instanceof RadioButton)) {
                int direction = position - lastPosition;
                if (direction < 0) {
                    checkNextRadioButton(position - 1);
                } else if (direction > 0) {
                    checkNextRadioButton(position + 1);
                }
            } else {
                ((RadioButton) child).setChecked(true);
            }
        }
    }

    public void destroyView() {
        viewPager.removeOnPageChangeListener(onViewPageChangedListener);
        viewPager = null;
        radioGroup.removeAllViews();
        radioGroup = null;
        viewPositionArr.clear();
        positionViewArr.clear();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (passDeal) {
            passDeal = false;
            return;
        }
        passDeal = true;

//        int position = 0;
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            LogUtil.info("点击rb-" + i + " - " + checkedId + " - " + radioGroup.getChildAt(i).getId());
//            if (checkedId == radioGroup.getChildAt(i).getId()) {
//                position = i;
//                break;
//            }
//        }
        viewPager.setCurrentItem(viewPositionArr.get(checkedId));


        passDeal = false;
    }
}
