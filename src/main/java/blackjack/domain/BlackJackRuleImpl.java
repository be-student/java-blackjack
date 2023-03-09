package blackjack.domain;

import blackjack.domain.participant.Dealer;
import blackjack.domain.participant.Player;

/**
 * 이 클래스가 룰이 자주 변경될 수 있기에, 인터페이스를 둔다는 점은 좋은데요
 * <p>
 * 사실상 한 클래스만 존재하는 상황에서, 이를 interface로 빼는 것은 별로 의미가 없을 수 있어보이는데, 어떻게 생각하시나요?
 */
public class BlackJackRuleImpl implements BlackJackRule {

    private static final int BUST_POINT = 21;

    @Override
    public ResultType calculateDealerResult(final Dealer dealer, final Player player) {
        //이 부분이 애매해서 리팩토링을 해보려고 했는데, 더 좋은 방법이 생각나지 않아서 일단 이렇게 구현했습니다.
        //enum 클래스에 BiPredicate를 추가해서 구현해보려고 했는데, enum 클래스에 메서드를 추가하는 것이 맞는지 잘 모르겠습니다.
        if (dealer.hasBlackJack()) {
            return playWithBlackjack(player);
        }
        if (dealer.currentScore() > BUST_POINT) {
            return playWithBust(player);
        }
        return playWithScore(dealer, player);
    }

    private ResultType playWithBlackjack(final Player player) {
        if (player.hasBlackJack()) {
            return ResultType.TIE;
        }
        return ResultType.BLACKJACK_WIN;
    }

    private ResultType playWithBust(final Player player) {
        if (player.currentScore() > BUST_POINT) {
            return ResultType.TIE;
        }
        if (player.hasBlackJack()) {
            return ResultType.BLACKJACK_LOSE;
        }
        return ResultType.LOSE;
    }

    private ResultType playWithScore(final Dealer dealer, final Player player) {
        if (player.hasBlackJack()) {
            return ResultType.BLACKJACK_LOSE;
        }
        if (player.currentScore() > BUST_POINT || dealer.currentScore() > player.currentScore()) {
            return ResultType.WIN;
        }
        return ResultType.LOSE;
    }
}
