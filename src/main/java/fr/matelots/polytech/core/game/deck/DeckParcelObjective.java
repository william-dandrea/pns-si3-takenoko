package fr.matelots.polytech.core.game.deck;

import fr.matelots.polytech.core.game.Board;
import fr.matelots.polytech.core.game.goalcards.CardObjectiveParcel;
import fr.matelots.polytech.core.game.goalcards.pattern.Patterns;
import fr.matelots.polytech.core.game.parcels.BambooColor;

/**
 * This class represent the deck of the parcels objectives who is composed of 15 objectives
 * @author Alexandre Arcil
 */
public class DeckParcelObjective extends Deck<CardObjectiveParcel> {

    public DeckParcelObjective(Board board) {
        super(board);
    }

    @Override
    protected void fill() {
        this.cards.add(new CardObjectiveParcel(board, 2, Patterns.TRIANGLE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN));
        this.cards.add(new CardObjectiveParcel(board, 3, Patterns.TRIANGLE, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.YELLOW));
        this.cards.add(new CardObjectiveParcel(board, 4, Patterns.TRIANGLE, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK));
        this.cards.add(new CardObjectiveParcel(board, 2, Patterns.LINE, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN));
        this.cards.add(new CardObjectiveParcel(board, 3, Patterns.LINE, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.YELLOW));
        this.cards.add(new CardObjectiveParcel(board, 4, Patterns.LINE, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK));
        this.cards.add(new CardObjectiveParcel(board, 2, Patterns.C, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN));
        this.cards.add(new CardObjectiveParcel(board, 3, Patterns.C, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.YELLOW));
        this.cards.add(new CardObjectiveParcel(board, 4, Patterns.C, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK));
        this.cards.add(new CardObjectiveParcel(board, 3, Patterns.RHOMBUS, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN, BambooColor.GREEN));
        this.cards.add(new CardObjectiveParcel(board, 3, Patterns.RHOMBUS, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.GREEN, BambooColor.GREEN));
        this.cards.add(new CardObjectiveParcel(board, 4, Patterns.RHOMBUS, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.YELLOW));
        this.cards.add(new CardObjectiveParcel(board, 4, Patterns.RHOMBUS, BambooColor.PINK, BambooColor.PINK, BambooColor.GREEN, BambooColor.GREEN));
        this.cards.add(new CardObjectiveParcel(board, 5, Patterns.RHOMBUS, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK, BambooColor.PINK));
        this.cards.add(new CardObjectiveParcel(board, 5, Patterns.RHOMBUS, BambooColor.YELLOW, BambooColor.YELLOW, BambooColor.PINK, BambooColor.PINK));
    }

}
