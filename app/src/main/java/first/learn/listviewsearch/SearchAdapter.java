package first.learn.listviewsearch;

/**
 * Created by bismillah on 11/1/2017.
 */

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchAdapter extends ArrayAdapter<String> {
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final int mResource;
    private List<String> mObjects;
    private int mFieldId = 0;
    private ArrayList<String> mOriginalValues;
    private ArrayFilter mFilter;
    private final Object mLock = new Object();
    private String mSearchText; // this var for highlight




    public SearchAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        mObjects = Arrays.asList(objects);
        mFieldId = textViewResourceId;
    }

    public SearchAdapter(@NonNull Context context, int resource,int textViewResourceId, @NonNull List<String> objects) {
        super(context, resource,textViewResourceId,  objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        mObjects = objects;
        mFieldId = textViewResourceId;
    }

    @NonNull
    @Override
    public Context getContext() {

        return mContext;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public String getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public int getPosition(String item) {
        return mObjects.indexOf(item);
    }

    String TAG = "searchOnubad";

    @Override
    public long getItemId(int position) {
        int itemID;

        if (mOriginalValues == null)
        {
            itemID = position;
        }
        else
        {
            itemID = mOriginalValues.indexOf(mObjects.get(position));
        }
        return itemID;

    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            int j =0;

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                mSearchText = "";
                ArrayList<String> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                mSearchText = prefixString;

                ArrayList<String> values;

                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                    }

                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final String value = values.get(i);
                    final String valueText = value.toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString) || valueText.contains(prefixString)) {
                        newValues.add(value);      j++;
                    } else {
                        final String[] word = valueText.split(" ");
                        final int wordCount = word.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {

                            if (word[k].startsWith(prefixString)|| word[k].contains(prefixString)) {
                                newValues.add(value);

                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


    public static CharSequence highlight(String searchText, String originalText) {

        //  String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

        int start = originalText.toLowerCase().indexOf(searchText.toLowerCase());
        if (start < 0) {
            // not found, nothing to to

            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + searchText.length(), originalText.length());


                System.out.println("similarityPercent : Start : "+start+" spanStart: "+spanStart+" spanEnd: "+spanEnd+" searchText.length(): "+searchText.length()+" originalText.length(): "+originalText.length());

                ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLUE});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                highlighted.setSpan(highlightSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = originalText.indexOf(searchText, spanEnd);
            }

            return highlighted;
        }
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextView textView;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.search_list_item, null);
        } else {
            view = convertView;
        }

        try {
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                textView = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                textView = (TextView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        // HIGHLIGHT...

             String fullText = getItem(position);

                if (mSearchText != null && !mSearchText.isEmpty()) {
                    assert fullText != null;
                    int startPos = fullText.toLowerCase().indexOf(mSearchText.toLowerCase());
                    int endPos = startPos + mSearchText.length();

                    if (startPos != -1) {

                        textView.setText(highlight(mSearchText, fullText));
                    } else {
                        textView.setText(fullText);
                    }
                } else {
                     textView.setText(fullText);
                }


        return view;
    }
}
