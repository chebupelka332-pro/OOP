package ru.nsu.tokarev.localization;

public class EnglishGameMessages implements GameMessages {

    @Override
    public String welcome() {
        return "Welcome to Blackjack!";
    }

    @Override
    public String round(int roundNumber) {
        return "\nRound " + roundNumber;
    }

    @Override
    public String dealerDealsCards() {
        return "Dealer deals cards";
    }

    @Override
    public String yourCards() {
        return "Your cards: ";
    }

    @Override
    public String dealerCards() {
        return "Dealer cards: ";
    }

    @Override
    public String hiddenCard() {
        return "<hidden card>";
    }

    @Override
    public String blackjackWin() {
        return "Blackjack! You won the round!";
    }

    @Override
    public String dealerBlackjack() {
        return "Dealer has blackjack! You lost the round.";
    }

    @Override
    public String yourTurn() {
        return "\nYour turn";
    }

    @Override
    public String hitOrStand() {
        return "Enter '1' to hit, '0' to stand";
    }

    @Override
    public String youDrewCard() {
        return "You drew card ";
    }

    @Override
    public String bust() {
        return "Bust! You lost the round.";
    }

    @Override
    public String dealerTurn() {
        return "\nDealer's turn";
    }

    @Override
    public String dealerRevealsCard() {
        return "Dealer reveals hidden card.";
    }

    @Override
    public String dealerDrawsCard() {
        return "Dealer draws card ";
    }

    @Override
    public String dealerBust() {
        return "Dealer busts! You won the round!";
    }

    @Override
    public String youWin() {
        return "You won the round!";
    }

    @Override
    public String youLose() {
        return "You lost the round.";
    }

    @Override
    public String tie() {
        return "It's a tie!";
    }

    @Override
    public String playAgain() {
        return "\nWould you like to play another round? (1 - yes, 0 - no)";
    }

    @Override
    public String thanksForPlaying() {
        return "Thanks for playing!";
    }

    @Override
    public String score(int playerScore, int dealerScore) {
        return String.format("Score %d:%d", playerScore, dealerScore);
    }

    @Override
    public String scoreInYourFavor(int playerScore, int dealerScore) {
        return String.format("Score %d:%d in your favor.", playerScore, dealerScore);
    }

    @Override
    public String scoreInDealerFavor(int playerScore, int dealerScore) {
        return String.format("Score %d:%d in dealer's favor.", playerScore, dealerScore);
    }

    @Override
    public String scoreTie(int playerScore, int dealerScore) {
        return String.format("Score %d:%d - tie.", playerScore, dealerScore);
    }
}