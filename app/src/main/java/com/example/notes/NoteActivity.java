package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.notes.models.NotesModel;
import com.example.notes.persistence.NoteRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener,
        TextWatcher {

    private static final String TAG = "NoteActivity";

    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;

    private LinedEditText mLinedEditText;
    private EditText mEditTitle;
    private TextView mViewTitle;
    private RelativeLayout mCheckContainer, mBackArrowConatainer;
    private ImageButton mChcek, mBackArrow;
    private GestureDetector mGestureDetector;
    private int mMode;

    private boolean mIsNewNote;
    private NotesModel mInitialNote;
    private NotesModel mFinalNote;

    private NoteRepository mNoteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mLinedEditText = findViewById(R.id.notesActivityEditText);
        mEditTitle = findViewById(R.id.note_title_et);
        mViewTitle = findViewById(R.id.note_title_tv);
        mCheckContainer = findViewById(R.id.checkmark_container);
        mBackArrowConatainer = findViewById(R.id.back_arrow_container);
        mChcek = findViewById(R.id.toolbar_check);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);

        mNoteRepository = new NoteRepository(this);

        if (getIncomingIntent()) {
            //new note (Edit mode)
            setNewNoteProperties();
            enableEditMode();
        } else {
            //not a new note (View mode)
            setNoteProperties();
            disableContentInteraction();
        }

        setListeners();
    }

    private void enableEditMode() {
        mBackArrowConatainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLED;

        enableContentInteraction();
    }

    private void disableEditMode() {
        mBackArrowConatainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);

        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        mMode = EDIT_MODE_DISABLED;

        disableContentInteraction();

        String temp = mLinedEditText.getText().toString();
        temp = temp.replace("\n","");
        temp = temp.replace(" ","");
        if(temp.length()>0)
        {
            mFinalNote.setTitle(mEditTitle.getText().toString());
            mFinalNote.setContent(mLinedEditText.getText().toString());
            String timestamp = getCurrentTimestamp();
            mFinalNote.setTimestamp(timestamp);
        }
        if(!mFinalNote.getContent().equals(mInitialNote.getContent()) ||
                !mFinalNote.getTitle().equals(mInitialNote.getTitle()))
        {
            saveChanges();
        }
    }

    private void hideSoftKeyboard()
    {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if(view == null)
        {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void setListeners() {
        mLinedEditText.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this, this);
        mViewTitle.setOnClickListener(this);
        mChcek.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        mEditTitle.addTextChangedListener(this);
    }

    public boolean getIncomingIntent() {
        if (getIntent().hasExtra("selected_note")) {
            mInitialNote = getIntent().getParcelableExtra("selected_note");

            mFinalNote = new NotesModel();
            mFinalNote.setTitle(mInitialNote.getTitle());
            mFinalNote.setContent(mInitialNote.getContent());
            mFinalNote.setTimestamp(mInitialNote.getTimestamp());
            mFinalNote.setId(mInitialNote.getId());

            mMode = EDIT_MODE_DISABLED;
            mIsNewNote = false;
            return false;
        }

        mMode = EDIT_MODE_ENABLED;
        mIsNewNote = true;
        return true;
    }

    private void saveChanges(){
        if(mIsNewNote)
        {
            saveNewNote();
        }
        else
        {
            updateNote();
        }
    }

    private void updateNote(){
        mNoteRepository.updateNote(mFinalNote);
    }

    private void saveNewNote(){
        mNoteRepository.insertNoteTask(mFinalNote);
    }

    private void setNewNoteProperties()
    {// new note
        mViewTitle.setText("Note Title");
        mEditTitle.setText("Note Title");

        mInitialNote = new NotesModel();
        mFinalNote = new NotesModel();
        mInitialNote.setTitle("Note Title");
        mFinalNote.setTitle("Note Title");
    }

    private void setNoteProperties()
    {//existing note
        mViewTitle.setText(mInitialNote.getTitle());
        mEditTitle.setText(mInitialNote.getTitle());
        mLinedEditText.setText(mInitialNote.getContent());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(TAG, "onSingleTapConfirmed: Single Tap Confirmed");
        //enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: Double tapped");
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.toolbar_check: {
                hideSoftKeyboard();
                disableEditMode();
                break;
            }
            case R.id.note_title_tv: {
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
            }
            case R.id.toolbar_back_arrow: {
                finish();
                break;
            }
        }

    }

    private void disableContentInteraction()
    {
        mLinedEditText.setKeyListener(null);
        mLinedEditText.setFocusable(false);
        mLinedEditText.setFocusableInTouchMode(false);
        mLinedEditText.setCursorVisible(false);
        mLinedEditText.clearFocus();
    }

    private void enableContentInteraction()
    {
        mLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditText.setFocusable(true);
        mLinedEditText.setFocusableInTouchMode(true);
        mLinedEditText.setCursorVisible(true);
        mLinedEditText.requestFocus();
    }

    @Override
    public void onBackPressed() {

        if(mMode == EDIT_MODE_ENABLED)
        {
            onClick(mChcek);
        }
        else {
            super.onBackPressed();
        }
    }

    public static String getCurrentTimestamp(){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String currentDateTime = simpleDateFormat.format(new Date());

            return currentDateTime;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        mViewTitle.setText(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}