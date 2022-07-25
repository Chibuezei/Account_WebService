package account.business;

import account.persistence.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LogService {
    @Autowired
    LogRepository logRepository;

    public void saveLog(Log log) {
        logRepository.save(log);
    }

    public List<Log> getAllLogs() {
        return logRepository.findAll();
    }
}
