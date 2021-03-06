package com.dkzy.areaparty.phone.fragment02;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.Model.MediaSetBean;
import com.dkzy.areaparty.phone.fragment02.base.SetAdapter;
import com.dkzy.areaparty.phone.fragment02.contentResolver.LocalSetListContainer;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_addFolder;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

/**
 * Project Name： FamilyCentralControler
 * Description:   图片列表界面
 * Author: boris
 * Time: 2017/1/6 16:20
 */

public class imageSetActivity extends Activity implements View.OnClickListener{

    private final String tag = this.getClass().getSimpleName();

    private ImageButton returnLogoIB;
    private RecyclerView fileSGV;
    private ImageView addNewSetIV;

    private View loadingView;
    private AlertDialog loadingDialog;

    SetAdapter fileAdapter;
    List<MediaSetBean> setList;
    List<MediaSetBean> setList_app;
    private String newSetName = "";
    private int setToDeleteId = -1;

    private TextView app_file,pc_file;
    private boolean isAppContent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab02_imageset_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initData();
        initView();
        initEvent();

        if (getIntent().getBooleanExtra("isAppContent",false) || !(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())){
            isAppContent = true;
            app_file.setTextColor(Color.parseColor("#FF5050"));
            app_file.setBackgroundResource(R.drawable.barback03_right_pressed);
            pc_file.setTextColor(Color.parseColor("#707070"));
            pc_file.setBackgroundResource(R.drawable.barback03_left_normal);
            fileAdapter.setNewData(setList_app);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.returnLogoIB:
                this.finish();
                break;
            case R.id.addNewSetIV:
                if (!isAppContent){
                    if(MyApplication.isSelectedPCOnline()) {
                        showAddSetDialog();
                    } else Toasty.info(imageSetActivity.this, "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }else {
                    showAddSetDialog();
                }
                break;
            case R.id.app_file:
                isAppContent = true;
                app_file.setTextColor(Color.parseColor("#FF5050"));
                app_file.setBackgroundResource(R.drawable.barback03_right_pressed);
                pc_file.setTextColor(Color.parseColor("#707070"));
                pc_file.setBackgroundResource(R.drawable.barback03_left_normal);
                fileAdapter.setNewData(setList_app);
                break;
            case R.id.pc_file:
                if (!(MyApplication.selectedPCVerified && MyApplication.isSelectedPCOnline())){
                    Toasty.warning(getApplicationContext(), "当前电脑不在线", Toast.LENGTH_SHORT, true).show();
                }else {
                    isAppContent = false;
                    pc_file.setTextColor(Color.parseColor("#FF5050"));
                    pc_file.setBackgroundResource(R.drawable.barback03_left_pressed);
                    app_file.setTextColor(Color.parseColor("#707070"));
                    app_file.setBackgroundResource(R.drawable.barback03_right_normal);
                    fileAdapter.setNewData(setList);
                }
                break;
        }
    }

    private void showAddSetDialog() {
        final ActionDialog_addFolder dialog = new ActionDialog_addFolder(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.setEditHintText("请输入图片列表名称");
        dialog.setTitleText("新建图片列表");
        final EditText editText = dialog.getEditTextView();
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempName = editText.getText().toString();
                if(tempName.equals("") || tempName.endsWith(".") ||
                        tempName.contains("\\") || tempName.contains("/") ||
                        tempName.contains(":")  || tempName.contains("*") ||
                        tempName.contains("?")  || tempName.contains("\"") ||
                        tempName.contains("<")  || tempName.contains(">") ||
                        tempName.contains("|")){
                    Toasty.error(imageSetActivity.this, "设备名不能为空，不能包含\\ / : * ? \" < > |字符", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isAppContent){
                        if(isSetContained(tempName)) {
                            Toasty.error(imageSetActivity.this, "当前列表名称已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            newSetName = tempName;
                            ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                            loadingDialog.show();
                            MediafileHelper.addImagePlaySet(tempName, myHandler);
                            dialog.dismiss();
                        }
                    }else {
                        if(isSetContained_app(tempName)) {
                            Toasty.error(imageSetActivity.this, "当前列表名称已存在", Toast.LENGTH_SHORT).show();
                        } else {
                            LocalSetListContainer.addLocalSetList("image",tempName);
                            setList_app.clear();
                            setList_app.addAll(LocalSetListContainer.getLocalSetList("image"));
                            fileAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            Toasty.success(imageSetActivity.this, "添加新的图片列表成功", Toast.LENGTH_SHORT, true).show();
                        }
                    }


                }
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private boolean isSetContained(String name) {
        for(int i = 0; i < setList.size(); ++i)
            if(setList.get(i).name.equals(name))
                return true;
        return false;
    }
    private boolean isSetContained_app(String name) {
        for(int i = 0; i < setList_app.size(); ++i)
            if(setList_app.get(i).name.equals(name))
                return true;
        return false;
    }

    /**
     * <summary>
     *  初始化数据
     * </summary>
     */
    private void initData() {
        setList = new ArrayList<>();
        if(MediafileHelper.imageSets.size() <= 0)
            MediafileHelper.loadMediaSets(myHandler);
        else {
            for(Map.Entry<String, List<MediaItem>> temp : MediafileHelper.imageSets.entrySet()) {
                MediaSetBean file = new MediaSetBean();
                file.name = temp.getKey();
                if(temp.getValue().size() > 0)
                    file.thumbnailURL = temp.getValue().get(0).getUrl();
                file.numInfor = temp.getValue().size() + "张";
                setList.add(file);
            }
        }

        fileAdapter = new SetAdapter(this, setList, R.layout.tab02_imageset_item, OrderConst.imageAction_name);
        fileAdapter.isFirstOnly(false);
        fileAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                //... 跳转到列表文件activity
                if (!isAppContent){
                    Intent intent = new Intent(imageSetActivity.this, imageSetContentActivity.class);
                    intent.putExtra("isAppContent",false);
                    intent.putExtra("setName", setList.get(i).name);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(imageSetActivity.this, imageSetContentActivity.class);
                    intent.putExtra("isAppContent",true);
                    intent.putExtra("setName", setList_app.get(i).name);
                    startActivity(intent);
                }

            }
        });
        fileAdapter.setOnRecyclerViewItemChildClickListener(new BaseQuickAdapter.OnRecyclerViewItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                if(view.getId() == R.id.deleteIV) {
                    if (!isAppContent){
                        ((TextView)(loadingView.findViewById(R.id.note))).setText("执行中...");
                        loadingDialog.show();
                        MediafileHelper.deleteImagePlaySet(setList.get(i).name, myHandler);
                        setToDeleteId = i;
                    }else {
                        LocalSetListContainer.deleteLocalSetList("image",setList_app.get(i).name);
                        setList_app.clear();
                        setList_app.addAll(LocalSetListContainer.getLocalSetList("image"));
                        fileAdapter.notifyDataSetChanged();
                        Toasty.success(imageSetActivity.this, "删除指定列表成功", Toast.LENGTH_SHORT, true).show();
                    }

                }
            }
        });

        setList_app = LocalSetListContainer.getLocalSetList("image");
    }



    /**
     * <summary>
     *  初始化控件
     * </summary>
     */
    private void initView() {

        app_file = (TextView) findViewById(R.id.app_file);
        pc_file = (TextView) findViewById(R.id.pc_file);

        returnLogoIB = (ImageButton) findViewById(R.id.returnLogoIB);
        addNewSetIV = (ImageView) findViewById(R.id.addNewSetIV);
        fileSGV = (RecyclerView) findViewById(R.id.fileSGV);
        fileSGV.setItemAnimator(new DefaultItemAnimator());
        fileSGV.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        fileSGV.setAdapter(fileAdapter);

        loadingView = LayoutInflater.from(this).inflate(R.layout.tab04_loadingcontent, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(loadingView).setCancelable(false);
        loadingDialog = builder.create();
    }

    /**
     * <summary>
     *  设置控件监听的事件
     * </summary>
     */
    private void initEvent() {
        returnLogoIB.setOnClickListener(this);
        addNewSetIV.setOnClickListener(this);

        app_file.setOnClickListener(this);
        pc_file.setOnClickListener(this);
    }


    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OrderConst.getPCImageSets_OK:
                    for(Map.Entry<String, List<MediaItem>> temp : MediafileHelper.imageSets.entrySet()) {
                        MediaSetBean file = new MediaSetBean();
                        file.name = temp.getKey();
                        if(temp.getValue().size() > 0)
                            file.thumbnailURL = temp.getValue().get(0).getThumbnailurl();
                        file.numInfor = temp.getValue().size() + "张";
                        setList.add(file);
                    }
                    fileAdapter.notifyDataSetChanged();
                    break;
                case OrderConst.getPCImageSets_Fail:
                    break;
                case OrderConst.addPCSet_OK:
                    if(!newSetName.equals("")) {
                        MediaSetBean file = new MediaSetBean();
                        file.name = newSetName;
                        file.numInfor = "0张";
                        setList.add(0, file);
                        MediafileHelper.imageSets.put(newSetName, new ArrayList<MediaItem>());
                    }
                    fileAdapter.notifyDataSetChanged();
                    loadingDialog.dismiss();
                    Toasty.success(imageSetActivity.this, "添加新的图片列表成功", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.addPCSet_Fail:
                    newSetName = "";
                    loadingDialog.dismiss();
                    Toasty.info(imageSetActivity.this, "添加新的图片列表失败", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.deletePCSet_OK:
                    if(setToDeleteId >= 0) {
                        MediafileHelper.imageSets.remove(setList.get(setToDeleteId).name);
                        setList.remove(setToDeleteId);
                        fileAdapter.notifyItemRemoved(setToDeleteId);
                    }
                    loadingDialog.dismiss();
                    Toasty.success(imageSetActivity.this, "删除指定列表成功", Toast.LENGTH_SHORT, true).show();
                    break;
                case OrderConst.deletePCSet_Fail:
                    loadingDialog.dismiss();
                    Toasty.error(imageSetActivity.this, "删除指定列表失败", Toast.LENGTH_SHORT, true).show();
                    break;
            }
        }
    };


}
