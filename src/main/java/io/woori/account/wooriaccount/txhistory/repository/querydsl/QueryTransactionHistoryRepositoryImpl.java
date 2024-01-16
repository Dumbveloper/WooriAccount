package io.woori.account.wooriaccount.txhistory.repository.querydsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.woori.account.wooriaccount.txhistory.domain.dto.FindAllDepositTxResponseDTO;
import io.woori.account.wooriaccount.txhistory.domain.dto.FindAllWithdrawTxResponseDTO;
import io.woori.account.wooriaccount.txhistory.domain.DepositTxHistory;
import io.woori.account.wooriaccount.txhistory.domain.WithdrawTxHistory;
import io.woori.account.wooriaccount.txhistory.domain.dto.QFindAllDepositTxResponseDTO;
import io.woori.account.wooriaccount.txhistory.domain.dto.QFindAllWithdrawTxResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

import static io.woori.account.wooriaccount.account.domain.entity.QAccount.account;
import static io.woori.account.wooriaccount.customer.domain.entity.QCustomer.customer;
import static io.woori.account.wooriaccount.txhistory.domain.QDepositTxHistory.depositTxHistory;
import static io.woori.account.wooriaccount.txhistory.domain.QWithdrawTxHistory.withdrawTxHistory;


/*
* queryDsl을 사용한 레포지토리 입니다.
* @author yeom hwiju
* */
@Repository
@RequiredArgsConstructor
public class QueryTransactionHistoryRepositoryImpl implements QueryTransactionHistoryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<DepositTxHistory> readDepositTxHistoryAllToList(Long accountId) {
        return jpaQueryFactory.select(
                        depositTxHistory
                ).from(depositTxHistory)
                .where(depositTxHistory.sender.accountId.eq(accountId))
                .innerJoin(depositTxHistory.receiver, account)
                .innerJoin(account.customer, customer)
                .orderBy(depositTxHistory.createdTime.desc())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }

    public List<WithdrawTxHistory> readWithdrawTxHistoryAllToList(Long accountId) {
        return jpaQueryFactory.select(
                        withdrawTxHistory
                ).from(withdrawTxHistory)
                .where(withdrawTxHistory.sender.accountId.eq(accountId))
                .innerJoin(withdrawTxHistory.receiver, account)
                .innerJoin(account.customer, customer)
                .orderBy(withdrawTxHistory.createdTime.desc())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }

    public Page<FindAllDepositTxResponseDTO> readDepositTxHistoryAll(Long accountId,
                                                                     Long lastTxHistoryId,
                                                                     Pageable pageable) {
        List<FindAllDepositTxResponseDTO> pageContent = jpaQueryFactory.select(
                new QFindAllDepositTxResponseDTO(
                    customer.customerName,
                    depositTxHistory.amount,
                    depositTxHistory.balanceAfterTx,
                    depositTxHistory.description,
                    depositTxHistory.createdTime
                )
        ).from(depositTxHistory)
         .where(depositTxHistory.receiver.accountId.eq(accountId).and(ltDepositTxHistoryId(lastTxHistoryId)))
         .innerJoin(depositTxHistory.sender, account)
         .innerJoin(account.customer, customer)
         .orderBy(depositTxHistory.createdTime.desc())
         .limit(pageable.getPageSize())
         .setLockMode(LockModeType.PESSIMISTIC_WRITE)
         .fetch();

        JPAQuery<Long> countDepositTxHistoryQuery = jpaQueryFactory
                .select(depositTxHistory.count())
                .from(depositTxHistory);

        return PageableExecutionUtils.getPage(pageContent, pageable, countDepositTxHistoryQuery::fetchOne);
    }

    private BooleanExpression ltDepositTxHistoryId(Long depositTxHistoryId) {
        if (depositTxHistoryId == null) {
            return null;
        }

        return depositTxHistory.txId.lt(depositTxHistoryId);
    }

    public Page<FindAllWithdrawTxResponseDTO> readWithdrawTxHistoryAll(Long accountId,
                                                                       Long lastTxHistoryId,
                                                                       Pageable pageable) {
        List<FindAllWithdrawTxResponseDTO> pageContent = jpaQueryFactory.select(
                        new QFindAllWithdrawTxResponseDTO(
                                customer.customerName,
                                withdrawTxHistory.amount,
                                withdrawTxHistory.balanceAfterTx,
                                withdrawTxHistory.description,
                                withdrawTxHistory.createdTime
                        )
                ).from(withdrawTxHistory)
                .where(withdrawTxHistory.receiver.accountId.eq(accountId).and(ltWithdrawTxHistoryId(lastTxHistoryId)))
                .innerJoin(withdrawTxHistory.sender, account)
                .innerJoin(account.customer, customer)
                .orderBy(withdrawTxHistory.createdTime.desc())
                .limit(pageable.getPageSize())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();

        JPAQuery<Long> countWithdrawTxHistoryQuery = jpaQueryFactory
                .select(withdrawTxHistory.count())
                .from(withdrawTxHistory);

        return PageableExecutionUtils.getPage(pageContent, pageable, countWithdrawTxHistoryQuery::fetchOne);
    }

    private BooleanExpression ltWithdrawTxHistoryId(Long withdrawTxHistoryId) {
        if (withdrawTxHistoryId == null) {
            return null;
        }

        return withdrawTxHistory.txId.lt(withdrawTxHistoryId);
    }

}