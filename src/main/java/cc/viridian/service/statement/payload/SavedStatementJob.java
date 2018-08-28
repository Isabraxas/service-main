package cc.viridian.service.statement.payload;

import cc.viridian.service.statement.persistence.StatementJob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class SavedStatementJob {

    private String id;
    private StatementJob statementJob;

}

