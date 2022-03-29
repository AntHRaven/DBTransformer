package dto;

import lombok.Data;

@Data
public class ForeignKey {
    private String toTable;
    private String fieldsFrom;
    private String fieldsTo;
}
