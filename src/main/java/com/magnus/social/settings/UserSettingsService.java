package com.magnus.social.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingsService {
  private final UserSettingsRepository userSettingsRepository;
}