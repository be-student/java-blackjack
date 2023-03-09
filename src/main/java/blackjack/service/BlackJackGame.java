package blackjack.service;

import blackjack.domain.BlackJackRule;
import blackjack.domain.ResultType;
import blackjack.domain.card.Card;
import blackjack.domain.card.Deck;
import blackjack.domain.card.DeckFactory;
import blackjack.domain.participant.Dealer;
import blackjack.domain.participant.Participants;
import blackjack.domain.participant.Player;
import blackjack.domain.participant.Players;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BlackJackGame {

    private final Participants participants;
    private final Deck deck;
    private final BlackJackRule blackJackRule;
    private final Map<String, Integer> playerMoney = new LinkedHashMap<>();

    private BlackJackGame(final Players players, final DeckFactory deckFactory, final BlackJackRule blackJackRule) {
        participants = new Participants(players, new Dealer());
        deck = deckFactory.generate();
        this.blackJackRule = blackJackRule;
    }

    public static BlackJackGame of(
            final List<String> playerNames,
            final DeckFactory deckFactory,
            final BlackJackRule blackJackRule) {
        return new BlackJackGame(Players.from(playerNames), deckFactory, blackJackRule);
    }

    public void distributeInitialCard() {
        participants.distributeInitialCards(deck);
    }

    public boolean isPlayerDrawable(final String playerName) {
        return participants.isPlayerDrawable(playerName);
    }

    public void drawPlayerCard(final String playerName) {
        participants.drawPlayerCard(playerName, deck.popCard());
    }

    public void drawDealerCard() {
        participants.drawDealerCard(deck.popCard());
    }

    public boolean isDealerDrawable() {
        return participants.isDealerDrawable();
    }

    public List<String> getPlayerNames() {
        return participants.getPlayerNames();
    }

    public Card getDealerFirstCard() {
        return participants.getDealer().getFirstCard();
    }

    public Players getPlayers() {
        return participants.getPlayers();
    }


    public List<Card> getPlayerCards(final String playerName) {
        return participants.findPlayerByName(playerName)
                .getCards();
    }

    public int getDealerScore() {
        return participants.getDealer()
                .currentScore();
    }

    public List<Card> getDealerCards() {
        return participants.getDealer()
                .getCards();
    }

    public Map<String, List<Card>> getPlayersCards() {
        final Map<String, List<Card>> playerCards = new HashMap<>();
        participants.getPlayers().getPlayers().forEach(player -> playerCards.put(player.getName(), player.getCards()));
        return playerCards;
    }

    public Map<String, Integer> getPlayersScores() {
        final Map<String, Integer> playerScores = new LinkedHashMap<>();
        participants.getPlayers().getPlayers()
                .forEach(player -> playerScores.put(player.getName(), player.currentScore()));
        return playerScores;
    }

    public void addPlayerMoney(final String playerName, final int inputPlayerMoney) {
        playerMoney.put(playerName, inputPlayerMoney);
    }

    public Map<String, Integer> calculatePlayersMoney() {
        final Map<String, Integer> resultPlayersMoney = new LinkedHashMap<>();
        final Dealer dealer = participants.getDealer();
        participants.getPlayers()
                .getPlayers()
                .forEach(calculateWinningMoney(resultPlayersMoney, dealer));
        return resultPlayersMoney;
    }

    private Consumer<Player> calculateWinningMoney(final Map<String, Integer> resultPlayersMoney, final Dealer dealer) {
        return player -> {
            final ResultType playerResult = blackJackRule.calculateDealerResult(dealer, player)
                    .getOppositeResult();
            final int betMoney = playerMoney.get(player.getName());
            final int playerWinningMoney = calculatePlayerMoney(betMoney, playerResult);
            resultPlayersMoney.put(player.getName(), playerWinningMoney);
        };
    }

    private int calculatePlayerMoney(final int money, final ResultType resultType) {
        return (int) (money * resultType.getPlayerProfit());
    }
}
