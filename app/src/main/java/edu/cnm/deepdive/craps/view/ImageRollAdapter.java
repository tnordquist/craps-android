package edu.cnm.deepdive.craps.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.cnm.deepdive.craps.R;
import edu.cnm.deepdive.craps.model.State;

public class ImageRollAdapter extends ArrayAdapter<int[]> {

  private Drawable[] faces;
  private State state;

  public ImageRollAdapter(Context context) {
    super(context, R.layout.image_roll_item);
    Resources res = context.getResources();
    String pkg = context.getPackageName();
    faces = new Drawable[6];
    for (int i = 0; i < 6; i++) {
      faces[i] = ContextCompat.getDrawable(context,
          res.getIdentifier("face_" + (i + 1), "drawable", pkg));
    }
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView,
      @NonNull ViewGroup parent) {
    View view;
    if (convertView == null) {
      view = LayoutInflater.from(getContext())
          .inflate(R.layout.image_roll_item, parent, false);
    } else {
      view = convertView;
    }
    int[] diceRoll = getItem(position);
    int diceSum = diceRoll[0] + diceRoll[1];
    ImageView die0 = view.findViewById(R.id.die_0);
    ImageView die1 = view.findViewById(R.id.die_1);
    TextView sum = view.findViewById(R.id.sum);
    die0.setImageDrawable(faces[diceRoll[0] - 1]);
    die1.setImageDrawable(faces[diceRoll[1] - 1]);
    sum.setText(getContext().getString(R.string.sum, diceSum));
    if (state == State.WIN) {
      view.setBackgroundColor(
          ContextCompat.getColor(getContext(), R.color.winColor));
    } else {
      view.setBackgroundColor(
          ContextCompat.getColor(getContext(), R.color.lossColor));
    }
    return view;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

}
