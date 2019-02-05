package edu.cnm.deepdive.craps.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import edu.cnm.deepdive.craps.R;
import edu.cnm.deepdive.craps.model.Game;
import edu.cnm.deepdive.craps.model.State;
import edu.cnm.deepdive.craps.view.ImageRollAdapter;
import edu.cnm.deepdive.craps.view.SimpleRollAdapter;
import java.util.List;
import java.util.Random;
import org.apache.commons.rng.simple.JDKRandomBridge;
import org.apache.commons.rng.simple.RandomSource;

public class MainActivity extends AppCompatActivity {

  private static final int SEED_SIZE = 312;

  private MenuItem playOne;
  private MenuItem playFast;
  private MenuItem pause;
  private MenuItem reset;
  private ListView rolls;
  private TextView tally;
  private ArrayAdapter<int[]> adapter;
  private boolean running;
  private Random rng;
  private Game game;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    rolls = findViewById(R.id.rolls);
    tally = findViewById(R.id.tally);
    adapter = new ImageRollAdapter(this);
    rolls.setAdapter(adapter);
    rng = new JDKRandomBridge(RandomSource.MT_64, RandomSource.createLongArray(SEED_SIZE));
    reset();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.options, menu);
    playOne = menu.findItem(R.id.play_one);
    playFast = menu.findItem(R.id.play_fast);
    pause = menu.findItem(R.id.pause);
    reset = menu.findItem(R.id.reset);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    playOne.setEnabled(!running);
    playOne.setVisible(!running);
    playFast.setEnabled(!running);
    playFast.setVisible(!running);
    pause.setEnabled(running);
    pause.setVisible(running);
    reset.setEnabled(!running);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = true;
    switch (item.getItemId()) {
      case R.id.play_one:
        playOne();
        break;
      case R.id.play_fast:
        playFast();
        break;
      case R.id.pause:
        pause();
        break;
      case R.id.reset:
        reset();
        break;
      default:
        handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

  private void playOne() {
    game.reset();
    game.play();
    updateRollsDisplay(game.getRolls(), game.getState());
    updateTallyDisplay(game.getWins(), game.getLosses());
  }

  private void playFast() {
    running = true;
    invalidateOptionsMenu();
    new Runner().start();
  }

  private void pause() {
    running = false;
  }

  private void reset() {
    game = new Game(rng);
    updateTallyDisplay(0, 0);
  }

  private void updateRollsDisplay(List<int[]> rolls, State state) {
    adapter.clear();
    ((ImageRollAdapter) adapter).setState(state);
    adapter.addAll(rolls);
  }

  private void updateTallyDisplay(long wins, long losses) {
    long plays = wins + losses;
    double percentage = (plays > 0) ? 100.0 * wins / plays : 0;
    String tallyString = getString(R.string.tally, wins, plays, percentage);
    tally.setText(tallyString);
  }

  private class Runner extends Thread {

    private long wins;
    private long losses;
    private int updateCycles;
    private List<int[]> rolls;
    private edu.cnm.deepdive.craps.model.State state;

    @Override
    public void run() {
      while (running) {
        for (int i = 0; i < 5000; i++) {
          game.reset();
          game.play();
        }
        updateCycles++;
        wins = game.getWins();
        losses = game.getLosses();
        if (updateCycles % 20 == 0) {
          rolls = game.getRolls();
          state = game.getState();
        } else {
          rolls = null;
          state = null;
        }
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            updateTallyDisplay(wins, losses);
            if (state != null) {
              updateRollsDisplay(rolls, state);
            }
          }
        });
      }
      invalidateOptionsMenu();
    }

  }

}
