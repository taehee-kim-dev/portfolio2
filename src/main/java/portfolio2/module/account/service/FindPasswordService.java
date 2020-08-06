package portfolio2.module.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.module.account.dto.request.FindPasswordRequestDto;
import portfolio2.module.account.service.process.FindPasswordProcess;

@Transactional
@RequiredArgsConstructor
@Service
public class FindPasswordService {

    private final FindPasswordProcess findPasswordProcess;

    public void sendFindPasswordEmail(FindPasswordRequestDto findPasswordRequestDto) {
        findPasswordProcess.sendFindPasswordEmail(findPasswordRequestDto);
    }
}
