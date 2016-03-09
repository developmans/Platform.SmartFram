package com.boxlab.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boxlab.platform.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PanelContent {

	/**
	 * An array of sample panel items.
	 */
	public static List<PanelItem> PANEL_ITEMS = new ArrayList<PanelItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, PanelItem> PANEL_ITEM_MAP = new HashMap<String, PanelItem>();

	static {
		// Add 3 sample items.
		addItem(new PanelItem("0", "控制台", R.drawable.alacarte_64px));
		addItem(new PanelItem("1", "智能生态调控", R.drawable.alacarte_64px));
		addItem(new PanelItem("2", "数据库管理", R.drawable.alacarte_64px));
		addItem(new PanelItem("3", "数据可视化", R.drawable.alacarte_64px));
	}

	private static void addItem(PanelItem item) {
		PANEL_ITEMS.add(item);
		PANEL_ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class PanelItem {
		public String id;
		public String content;
		public int drawableResourceID;

		public PanelItem(String id, String content, int drawableResourceID) {
			this.id = id;
			this.content = content;
			this.drawableResourceID = drawableResourceID;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
