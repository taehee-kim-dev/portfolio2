package portfolio2.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio2.domain.process.account.FindPasswordProcess;
import portfolio2.dto.request.account.FindPasswordRequestDto;

@Transactional
@RequiredArgsConstructor
@Service
public class FindPasswordService {

    private final FindPasswordProcess findPasswordProcess;

    public void sendFindPasswordEmail(FindPasswordRequestDto findPasswordRequestDto) {
        findPasswordProcess.sendFindPasswordEmail(findPasswordRequestDto);
    }
}
