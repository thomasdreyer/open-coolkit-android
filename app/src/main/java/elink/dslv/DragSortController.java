package elink.dslv;

import demo.demo.R;
import com.coolkit.common.HLog;

import elink.HkConst;
import elink.activity.DeviceActivity;
import elink.model.DeviceModel.ItemInfo;

import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.LinearLayout;

/**
 * Class that starts and stops item drags on a {@link DragSortListView} based on
 * touch gestures. This class also inherits from {@link SimpleFloatViewManager},
 * which provides basic float View creation.
 * 
 * An instance of this class is meant to be passed to the methods
 * {@link DragSortListView#setTouchListener()} and
 * {@link DragSortListView#setFloatViewManager()} of your
 * {@link DragSortListView} instance.
 */
public class DragSortController extends SimpleFloatViewManager implements
		View.OnTouchListener, GestureDetector.OnGestureListener {

	/**
	 * Drag init mode enum.
	 */
	public static final int ON_DOWN = 0;
	public static final int ON_DRAG = 1;
	public static final int ON_LONG_PRESS = 2;

	private boolean mSortEnabled = true;

	/**
	 * Remove mode enum.
	 */
	public static final int CLICK_REMOVE = 0;
	public static final int FLING_REMOVE = 1;

	/**
	 * The current remove mode.
	 */
	private int mRemoveMode;

	private boolean mRemoveEnabled = false;
	private boolean mIsRemoving = false;

	private GestureDetector mDetector;

	private GestureDetector mFlingRemoveDetector;

	private int mTouchSlop;

	public static final int MISS = -1;
	private static final String TAG = null;

	private int mHitPos = MISS;
	private int mFlingHitPos = MISS;

	private int mClickRemoveHitPos = MISS;

	private int[] mTempLoc = new int[2];

	private int mItemX;
	private int mItemY;

	private int mCurrX;
	private int mCurrY;

	private boolean mDragging = false;

	private float mFlingSpeed = 500f;

	private int mDragHandleId;

	private int mClickRemoveId;

	private int mFlingHandleId;
	private boolean mCanDrag;
	private int mRightViewWidth = 280;
	private DragSortListView mDslv;
	private int mPositionX;
	private boolean mIsHorizontal;
	private boolean fligShow;

	/**
	 * Calls {@link #DragSortController(DragSortListView, int)} with a 0 drag
	 * handle id, FLING_RIGHT_REMOVE remove mode, and ON_DOWN drag init. By
	 * default, sorting is enabled, and removal is disabled.
	 * 
	 * @param dslv
	 *            The DSLV instance
	 */
	public DragSortController(DragSortListView dslv) {
		this(dslv, 0, ON_DOWN, FLING_REMOVE);
	}

	public DragSortController(DragSortListView dslv, int dragHandleId,
			int dragInitMode, int removeMode) {
		this(dslv, dragHandleId, dragInitMode, removeMode, 0);
	}

	public DragSortController(DragSortListView dslv, int dragHandleId,
			int dragInitMode, int removeMode, int clickRemoveId) {
		this(dslv, dragHandleId, dragInitMode, removeMode, clickRemoveId, 0);
	}

	/**
	 * By default, sorting is enabled, and removal is disabled.
	 * 
	 * @param dslv
	 *            The DSLV instance
	 * @param dragHandleId
	 *            The resource id of the View that represents the drag handle in
	 *            a list item.
	 */
	public DragSortController(DragSortListView dslv, int dragHandleId,
			int dragInitMode, int removeMode, int clickRemoveId,
			int flingHandleId) {
		super(dslv);
		mDslv = dslv;
		mDetector = new GestureDetector(dslv.getContext(), this);
		mFlingRemoveDetector = new GestureDetector(dslv.getContext(),
				mFlingRemoveListener);
		mFlingRemoveDetector.setIsLongpressEnabled(false);
		mTouchSlop = ViewConfiguration.get(dslv.getContext())
				.getScaledTouchSlop();
		mDragHandleId = dragHandleId;
		mClickRemoveId = clickRemoveId;
		mFlingHandleId = flingHandleId;
		setRemoveMode(removeMode);
	}

	/**
	 * Enable/Disable list item sorting. Disabling is useful if only item
	 * removal is desired. Prevents drags in the vertical direction.
	 * 
	 * @param enabled
	 *            Set <code>true</code> to enable list item sorting.
	 */
	public void setSortEnabled(boolean enabled) {
		mSortEnabled = enabled;
	}

	public boolean isSortEnabled() {
		return mSortEnabled;
	}

	/**
	 * One of {@link CLICK_REMOVE}, {@link FLING_RIGHT_REMOVE},
	 * {@link FLING_LEFT_REMOVE}, {@link SLIDE_RIGHT_REMOVE}, or
	 * {@link SLIDE_LEFT_REMOVE}.
	 */
	public void setRemoveMode(int mode) {
		mRemoveMode = mode;
	}

	public int getRemoveMode() {
		return mRemoveMode;
	}

	/**
	 * Enable/Disable item removal without affecting remove mode.
	 */
	public void setRemoveEnabled(boolean enabled) {
		mRemoveEnabled = enabled;
	}

	public boolean isRemoveEnabled() {
		return mRemoveEnabled;
	}

	/**
	 * Set the resource id for the View that represents the drag handle in a
	 * list item.
	 * 
	 * @param id
	 *            An android resource id.
	 */
	public void setDragHandleId(int id) {
		mDragHandleId = id;
	}

	/**
	 * Set the resource id for the View that represents the fling handle in a
	 * list item.
	 * 
	 * @param id
	 *            An android resource id.
	 */
	public void setFlingHandleId(int id) {
		mFlingHandleId = id;
	}

	/**
	 * Set the resource id for the View that represents click removal button.
	 * 
	 * @param id
	 *            An android resource id.
	 */
	public void setClickRemoveId(int id) {
		mClickRemoveId = id;
	}

	/**
	 * Sets flags to restrict certain motions of the floating View based on
	 * DragSortController settings (such as remove mode). Starts the drag on the
	 * DragSortListView.
	 * 
	 * @param position
	 *            The list item position (includes headers).
	 * @param deltaX
	 *            Touch x-coord minus left edge of floating View.
	 * @param deltaY
	 *            Touch y-coord minus top edge of floating View.
	 * 
	 * @return True if drag started, false otherwise.
	 */
	public boolean startDrag(int position, int deltaX, int deltaY) {

		int dragFlags = 0;
		if (mSortEnabled && !mIsRemoving) {
			dragFlags |= DragSortListView.DRAG_POS_Y
					| DragSortListView.DRAG_NEG_Y;
		}
		if (mRemoveEnabled && mIsRemoving) {
			dragFlags |= DragSortListView.DRAG_POS_X;
			dragFlags |= DragSortListView.DRAG_NEG_X;
		}

		mDragging = mDslv.startDrag(position - mDslv.getHeaderViewsCount(),
				dragFlags, deltaX, deltaY);
		return mDragging;
	}

	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		HLog.i(TAG,
				"onTouch:mDslv.listViewIntercepted()"
						+ mDslv.listViewIntercepted());
		if (!mDslv.isDragEnabled() || mDslv.listViewIntercepted()) {
			return false;
		}

		mDetector.onTouchEvent(ev);
		if (mRemoveEnabled && mDragging && mRemoveMode == FLING_REMOVE) {
			mFlingRemoveDetector.onTouchEvent(ev);
		}

		int action = ev.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mCurrX = (int) ev.getX();
			mCurrY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			HLog.i(TAG, "onTouch move");
			break;
		case MotionEvent.ACTION_UP:
			if (mRemoveEnabled && mIsRemoving) {
				int x = mPositionX >= 0 ? mPositionX : -mPositionX;
				int removePoint = mDslv.getWidth() / 2;
				if (x > removePoint) {
					mDslv.stopDragWithVelocity(true, 0);
				}
			}
		case MotionEvent.ACTION_CANCEL:
			mIsRemoving = false;
			mDragging = false;
			break;
		}

		return false;
	}

	/**
	 * Overrides to provide fading when slide removal is enabled.
	 */
	@Override
	public void onDragFloatView(View floatView, Point position, Point touch) {

		if (mRemoveEnabled && mIsRemoving) {
			mPositionX = position.x;
		}
	}

	/**
	 * Get the position to start dragging based on the ACTION_DOWN MotionEvent.
	 * This function simply calls {@link #dragHandleHitPosition(MotionEvent)}.
	 * Override to change drag handle behavior; this function is called
	 * internally when an ACTION_DOWN event is detected.
	 * 
	 * @param ev
	 *            The ACTION_DOWN MotionEvent.
	 * 
	 * @return The list position to drag if a drag-init gesture is detected;
	 *         MISS if unsuccessful.
	 */
	public int startDragPosition(MotionEvent ev) {
		return dragHandleHitPosition(ev);
	}

	public int startFlingPosition(MotionEvent ev) {
		return mRemoveMode == FLING_REMOVE ? flingHandleHitPosition(ev) : MISS;
	}

	/**
	 * Checks for the touch of an item's drag handle (specified by
	 * {@link #setDragHandleId(int)}), and returns that item's position if a
	 * drag handle touch was detected.
	 * 
	 * @param ev
	 *            The ACTION_DOWN MotionEvent.
	 * 
	 * @return The list position of the item whose drag handle was touched; MISS
	 *         if unsuccessful.
	 */
	public int dragHandleHitPosition(MotionEvent ev) {
		return viewIdHitPosition(ev, mDragHandleId);
	}

	public int flingHandleHitPosition(MotionEvent ev) {
		return viewIdHitPosition(ev, mFlingHandleId);
	}

	public int viewIdHitPosition(MotionEvent ev, int id) {
		final int x = (int) ev.getX();
		final int y = (int) ev.getY();

		int touchPos = mDslv.pointToPosition(x, y); // includes headers/footers

		final int numHeaders = mDslv.getHeaderViewsCount();
		final int numFooters = mDslv.getFooterViewsCount();
		final int count = mDslv.getCount();

		// Log.d("mobeta", "touch down on position " + itemnum);
		// We're only interested if the touch was on an
		// item that's not a header or footer.
		if (touchPos != AdapterView.INVALID_POSITION && touchPos >= numHeaders
				&& touchPos < (count - numFooters)) {
			mDslv.mTouchItem = (DragSortItemView) mDslv.getChildAt(touchPos
					- mDslv.getFirstVisiblePosition());
			final int rawX = (int) ev.getRawX();
			final int rawY = (int) ev.getRawY();

			View dragBox = id == 0 ? mDslv.mTouchItem : (View) mDslv.mTouchItem
					.findViewById(id);
			if (dragBox != null) {
				dragBox.getLocationOnScreen(mTempLoc);

				if (rawX > mTempLoc[0] && rawY > mTempLoc[1]
						&& rawX < mTempLoc[0] + dragBox.getWidth()
						&& rawY < mTempLoc[1] + dragBox.getHeight()) {

					mItemX = mDslv.mTouchItem.getLeft();
					mItemY = mDslv.mTouchItem.getTop();

					return touchPos;
				}
			}
		}

		return MISS;
	}

	@Override
	public boolean onDown(MotionEvent ev) {
		HLog.i(TAG, "on onDown");

		viewIdHitPosition(ev, 0);

		mHitPos = startDragPosition(ev);

		// if (mHitPos != MISS) {
		// startDrag(mHitPos, (int) ev.getX() - mItemX, (int) ev.getY()
		// - mItemY);
		// }

		mIsRemoving = false;
		mCanDrag = true;
		mPositionX = 0;
		mFlingHitPos = startFlingPosition(ev);

		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		HLog.i(TAG, "on scroll");
		if (e1 == null || e2 == null) {
			HLog.i(TAG, "on scroll return");
			return false;
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		HLog.i(TAG, " on long press");

	}

	// complete the OnGestureListener interface
	@Override
	public final boolean onFling(MotionEvent e1, MotionEvent e2,
			float velocityX, float velocityY) {
		HLog.i(TAG,"on flig list view item");
		float dx = e2.getX() - e1.getX();
		float dy = e2.getY() - e1.getY();

		if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
			mIsHorizontal = true;
		} else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
			mIsHorizontal = false;
		}

		if (null != mDslv.mTouchItem && mDslv.mTouchItem.getChildCount() > 0) {
			ItemInfo info = (ItemInfo) mDslv.mTouchItem.getChildAt(0).getTag();
			if (null != info && HkConst.ITEM_TYPE_DEVICE == info.type
					&& mIsHorizontal) {
				// hide
				if (dx > 0 && info.isScollShow) {
					mDslv.mTouchItem.getChildAt(0).scrollTo(0, 0);
					info.isScollShow = false;
				} else
				// show
				if (dx < 0 && !info.isScollShow) {
					info.isScollShow = true;
					int x = (int) (mDslv.getContext().getResources().getDimension(R.dimen.di_btn_width) * info.scollUiCount);
					mDslv.mTouchItem.getChildAt(0).scrollBy(x, 0);
				}

			}

		}

		HLog.i(TAG,
				"on fling : dx:" + (e2.getX() - e1.getX()) + " dy:"
						+ (e2.getY() - e1.getY()));
		return false;
	}

	public boolean checkIsClick(MotionEvent ev, View target, ItemInfo info,
			boolean isLeft) {
		float source = ev.getRawX() + mDslv.getContext().getResources().getDimension(R.dimen.di_btn_width)  * info.scollUiCount;

		int[] location = new int[2];
		target.getLocationInWindow(location);

		float x = location[0];

		float xRange = x + target.getWidth();

		float y = location[1];
		float yRange = y + target.getHeight();
		return x < source && xRange > source && y < ev.getRawY()
				&& yRange > ev.getRawY();
	}

	// complete the OnGestureListener interface
	@Override
	public boolean onSingleTapUp(MotionEvent ev) {

		//
		

		
		if (null != mDslv.mTouchItem && mDslv.mTouchItem.getChildCount() > 0) {
			int[] location = new int[2];
			mDslv.mTouchItem.getLocationInWindow(location);
			
			if(ev.getRawY()>location[1]+mDslv.mTouchItem.getHeight()){
			return false;	
			}
			ItemInfo info = (ItemInfo) mDslv.mTouchItem.getChildAt(0).getTag();
			if (null != info && HkConst.ITEM_TYPE_DEVICE == info.type) {
				LinearLayout layout = (LinearLayout) mDslv.mTouchItem
						.getChildAt(0);
				boolean gotoDetail = checkIsClick(ev, layout.getChildAt(0),
						info, true);
				if (gotoDetail || !info.isScollShow) {

					((DeviceActivity) mDslv.getContext()).mDeviceListFragment.onSingleTapUp(info);
				}

				HLog.i(TAG,
						" layout.getChildAt(0):"
								+ checkIsClick(ev, layout.getChildAt(0), info,
										true));

				HLog.i(TAG,
						" layout.getChildAt(1):"
								+ checkIsClick(ev, layout.getChildAt(1), info,
										false));
				HLog.i(TAG,
						" layout.getChildAt(2):"
								+ checkIsClick(ev, layout.getChildAt(2), info,
										false));
			}

		}
		return true;
	}

	// complete the OnGestureListener interface
	@Override
	public void onShowPress(MotionEvent ev) {
		View view = mDslv.getChildAt(mHitPos);
		// HLog.i(TAG, "select i:"+i);
		if (mHitPos != MISS) {
			startDrag(mHitPos, mCurrX - mItemX, mCurrY - mItemY);
		}
	}

	/**
	 * @param dx
	 * @param dy
	 * @return judge if can judge scroll direction
	 */
	private boolean judgeScrollDirection(float dx, float dy) {
		boolean canJudge = true;
		fligShow = false;
		if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
			if (dx < 0) {
				fligShow = true;

			}
			mIsHorizontal = true;
		} else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
			mIsHorizontal = false;
		} else {
			canJudge = false;
		}

		return canJudge;
	}

	private GestureDetector.OnGestureListener mFlingRemoveListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public final boolean onFling(MotionEvent e1, MotionEvent e2,
				float velocityX, float velocityY) {
			// Log.d("mobeta", "on fling remove called");
			if (mRemoveEnabled && mIsRemoving) {
				int w = mDslv.getWidth();
				int minPos = w / 5;
				if (velocityX > mFlingSpeed) {
					if (mPositionX > -minPos) {
						mDslv.stopDragWithVelocity(true, velocityX);
					}
				} else if (velocityX < -mFlingSpeed) {
					if (mPositionX < minPos) {
						mDslv.stopDragWithVelocity(true, velocityX);
					}
				}
				mIsRemoving = false;
			}
			return false;
		}
	};

}
