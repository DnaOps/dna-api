package dgu.edu.dnaapi.util;

import dgu.edu.dnaapi.domain.BoardSearchCriteria;
import dgu.edu.dnaapi.exception.DNACustomException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import static dgu.edu.dnaapi.domain.response.DnaStatusCode.INVALID_SEARCH_OPTION;

public class StringToBoardSearchCriteriaConverter implements Converter<String, BoardSearchCriteria> {

    @Override
    public BoardSearchCriteria convert(String source) {
        for (BoardSearchCriteria value : BoardSearchCriteria.values()) {
            if (value.getSearchCriteria().equals(source))
                return value;
        }
        System.out.println("source = " + source);
        throw new DNACustomException("Invalid Search Option", INVALID_SEARCH_OPTION);
    }
}
