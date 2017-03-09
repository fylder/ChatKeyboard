package fylder.keyboard.lib.view.imp;


import fylder.keyboard.lib.bean.EmoticonBean;

public interface IView {

    void onItemClick(EmoticonBean bean);

    void onPageChangeTo(int position);
}
