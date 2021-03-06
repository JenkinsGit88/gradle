/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.transform;

import org.gradle.internal.operations.BuildOperationCategory;
import org.gradle.internal.operations.BuildOperationContext;
import org.gradle.internal.operations.BuildOperationDescriptor;
import org.gradle.internal.operations.RunnableBuildOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

class TransformFileOperation implements RunnableBuildOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformFileOperation.class);
    private final File file;
    private final ArtifactTransformer transform;
    private final BuildOperationCategory category;
    private Throwable failure;
    private List<File> result;

    TransformFileOperation(File file, ArtifactTransformer transform, BuildOperationCategory category) {
        this.file = file;
        this.transform = transform;
        this.category = category;
    }

    @Override
    public void run(@Nullable BuildOperationContext context) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Executing transform {} on file {}", transform.getDisplayName(), file);
            }
            result = transform.transform(file);
        } catch (Throwable t) {
            failure = t;
        }
    }

    @Override
    public BuildOperationDescriptor.Builder description() {
        String displayName = "Transform " + file.getName() + " with " + transform.getDisplayName();
        return BuildOperationDescriptor.displayName(displayName)
            .progressDisplayName(displayName)
            .operationType(category);
    }

    @Nullable
    public Throwable getFailure() {
        return failure;
    }

    @Nullable
    public List<File> getResult() {
        return result;
    }
}
