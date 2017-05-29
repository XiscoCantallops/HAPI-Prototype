package optum.com.smartprototype.scroll;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import optum.com.smartprototype.R;


/**
 * Created by gedison on 5/28/2017.
 */

public class EndOfPageScrollListener implements AbsListView.OnScrollListener {
    private OnScrollToEndOfPage parent;

    public EndOfPageScrollListener(OnScrollToEndOfPage parent){
        this.parent = parent;
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {}

    public void onScroll(AbsListView lw, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;
        if(lastItem == totalItemCount) parent.onScrollToEndOfPage();
    }
}
