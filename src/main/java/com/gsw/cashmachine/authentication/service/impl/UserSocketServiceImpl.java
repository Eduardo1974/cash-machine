package com.gsw.cashmachine.authentication.service.impl;

import com.gsw.cashmachine.authentication.request.AuthenticationSocketRequest;
import com.gsw.cashmachine.authentication.response.AuthenticationSocketResponse;
import com.gsw.cashmachine.authentication.service.UserSocketService;
import com.gsw.cashmachine.authentication.exception.AuthenticationSocketException;
import com.gsw.cashmachine.utils.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A classe UserSocketService e reponsavel por gerenciar as sessoes do servidor.
 *
 * @author Eduardo Alves
 * @version 1.0
 */
@Service
public class UserSocketServiceImpl implements UserSocketService {

    @Autowired
    private SimpMessagingTemplate simpTemplate;

    private final Log logger = LogFactory.getLog(UserSocketServiceImpl.class);

    private Map<String, AuthenticationSocketRequest> usersMap;

    @Override
    public void connectUser(final AuthenticationSocketRequest request) {
        try {
            addUser(request);
            sendMessage(Constants.JOIN, request, checkLimitConnectedUsers(request));
        } catch (AuthenticationSocketException e) {
            sendMessage(Constants.JOIN, request, e.getMessage());
            logger.error(e.getMessage());
        }
        logger.info("Connected profile: " + usersMap.size());
    }

    @Override
    public void disconnectUser(final AuthenticationSocketRequest request) {
        if (request.getSessionId() != null) {
            Iterator<String> iterate = usersMap.keySet().iterator();

            while (iterate.hasNext()) {
                AuthenticationSocketRequest authentication = usersMap.get(iterate.next());
                if (request.getSessionId().equals(authentication.getSessionId())) {
                    iterate.remove();
                    sendMessage(Constants.LEAVE, authentication, "disconnect");
                    break;
                }
            }
        }
        logger.info("Connected profile: " + usersMap.size());
    }

    private void sendMessage(final String constants, final AuthenticationSocketRequest request, final String message) {
        simpTemplate.convertAndSend(Constants.DESTINATION + constants + "/" + request.getToken(),
                new AuthenticationSocketResponse(request.getSessionId(), message));
    }

    private String checkLimitConnectedUsers(final AuthenticationSocketRequest request) throws AuthenticationSocketException {
        logger.info("checkLimitConnectedUsers: " + usersMap.size());
        if (usersMap.size() > 5) {
            throw new AuthenticationSocketException("User limit exceeded");
        } else {
            usersMap.put(request.getUsername(), request);
            return "joined";
        }
    }

    private void addUser(final AuthenticationSocketRequest request) throws AuthenticationSocketException {
        if ( usersMap.containsKey(request.getUsername())) {
            throw new AuthenticationSocketException("User already connected");
        } else {
            usersMap.put(request.getUsername(), request);
        }
    }

    @Bean
    private Map<String, AuthenticationSocketRequest> getUsersMap() {
        return usersMap = new HashMap<>();
    }

}
