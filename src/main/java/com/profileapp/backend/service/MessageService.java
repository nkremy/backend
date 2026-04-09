package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.MessageRequestDTO;
import com.profileapp.backend.dto.response.detail.MessageDetailDTO;
import com.profileapp.backend.dto.response.summary.MessageSummaryDTO;
import java.util.List;

public interface MessageService {
    MessageDetailDTO createMessage(MessageRequestDTO requestDTO);
    List<MessageSummaryDTO> getAllMessages();
    MessageDetailDTO getMessageById(Long id);
    void deleteMessage(Long id);
}
