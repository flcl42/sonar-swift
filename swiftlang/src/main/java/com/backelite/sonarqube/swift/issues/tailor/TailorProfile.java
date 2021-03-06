/**
 * Swift SonarQube Plugin - Swift module - Enables analysis of Swift and Objective-C projects into SonarQube.
 * Copyright © 2015 Backelite (${email})
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.backelite.sonarqube.swift.issues.tailor;

import com.backelite.sonarqube.swift.lang.core.Swift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.ValidationMessages;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by tzwickl on 22/11/2016.
 */

public class TailorProfile implements BuiltInQualityProfilesDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(TailorProfile.class);
    public static final String PROFILE_PATH = "/org/sonar/plugins/tailor/profile-tailor.xml";

    private final TailorProfileImporter profileImporter;

    public TailorProfile(final TailorProfileImporter importer) {
        this.profileImporter = importer;
    }

    @Override
    public void define(Context context) {
        LOGGER.info("Creating Tailor Profile");
        NewBuiltInQualityProfile nbiqp = context.createBuiltInQualityProfile(TailorRulesDefinition.REPOSITORY_KEY, Swift.KEY);
        try(Reader config = new InputStreamReader(getClass().getResourceAsStream(PROFILE_PATH))) {
            RulesProfile ocLintRulesProfile = profileImporter.importProfile(config, ValidationMessages.create());
            for (ActiveRule rule : ocLintRulesProfile.getActiveRules()) {
                nbiqp.activateRule(rule.getRepositoryKey(), rule.getRuleKey());
            }
        } catch (IOException ex){
            LOGGER.error("Error Creating Tailor Profile",ex);
        }
        nbiqp.done();
    }
}
