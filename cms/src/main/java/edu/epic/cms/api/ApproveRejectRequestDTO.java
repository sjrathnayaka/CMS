package edu.epic.cms.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for approving or rejecting a card request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveRejectRequestDTO {
    
    private String remark;  // Optional remark for approval/rejection
}
