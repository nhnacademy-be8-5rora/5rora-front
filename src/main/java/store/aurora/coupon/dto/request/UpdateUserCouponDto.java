package store.aurora.coupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import store.aurora.coupon.dto.CouponState;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateUserCouponDto {
    @NotNull
    private List<String> userIds;    // 바꿀 유저 쿠폰 ID 리스트
    private Long policyId; // 바꿀 정책의 ID
    private CouponState state;   // 변경할 쿠폰 상태
    private LocalDate endDate;   // 종료일
}
