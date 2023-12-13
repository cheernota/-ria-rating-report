package com.cheernota.riaratingreport.util;

import com.cheernota.riaratingreport.dto.request.ResearchFilterRqDto;
import com.cheernota.riaratingreport.entity.Research;
import com.cheernota.riaratingreport.entity.Research_;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class ResearchSpecification {

    public static Specification<Research> buildResearchSpecification(ResearchFilterRqDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            Predicate dateFromPredicate = cb.greaterThanOrEqualTo(root.get(Research_.researchDate), filter.getDateFrom());
            predicateList.add(dateFromPredicate);

            Predicate dateToPredicate;
            if (filter.getDateTo() != null) {
                dateToPredicate = cb.lessThanOrEqualTo(root.get(Research_.researchDate), filter.getDateTo());
                predicateList.add(dateToPredicate);
            }

            Predicate textPredicate;
            if (StringUtils.isNotEmpty(filter.getText())) {
                textPredicate = cb.like(cb.lower(root.get(Research_.researchName)), "%" + filter.getText().toLowerCase() + "%");
                predicateList.add(textPredicate);
            }

            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
