/*
 * Copyright (C) 2020 The Android Open Source Project
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
package com.google.android.exoplayer2.robolectric;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.rules.ExternalResource;

public final class ShadowMediaCodecConfig extends ExternalResource {

  public static ShadowMediaCodecConfig forAllSupportedMimeTypes() {
    return new ShadowMediaCodecConfig();
  }

  @Override
  protected void before() throws Throwable {
    // Video codecs
    MediaCodecInfo.CodecProfileLevel avcProfileLevel =
        createProfileLevel(
            MediaCodecInfo.CodecProfileLevel.AVCProfileHigh,
            MediaCodecInfo.CodecProfileLevel.AVCLevel62);
    configureCodec(
        /* codecName= */ "exotest.video.avc",
        MimeTypes.VIDEO_H264,
        ImmutableList.of(avcProfileLevel),
        ImmutableList.of(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible));
    MediaCodecInfo.CodecProfileLevel mpeg2ProfileLevel =
        createProfileLevel(
            MediaCodecInfo.CodecProfileLevel.MPEG2ProfileMain,
            MediaCodecInfo.CodecProfileLevel.MPEG2LevelML);
    configureCodec(
        /* codecName= */ "exotest.video.mpeg2",
        MimeTypes.VIDEO_MPEG2,
        ImmutableList.of(mpeg2ProfileLevel),
        ImmutableList.of(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible));
    configureCodec(
        /* codecName= */ "exotest.video.vp9",
        MimeTypes.VIDEO_VP9,
        ImmutableList.of(),
        ImmutableList.of(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible));

    // Audio codecs
    configureCodec("exotest.audio.aac", MimeTypes.AUDIO_AAC);
    configureCodec("exotest.audio.ac3", MimeTypes.AUDIO_AC3);
    configureCodec("exotest.audio.ac4", MimeTypes.AUDIO_AC4);
    configureCodec("exotest.audio.eac3", MimeTypes.AUDIO_E_AC3);
    configureCodec("exotest.audio.eac3joc", MimeTypes.AUDIO_E_AC3_JOC);
    configureCodec("exotest.audio.flac", MimeTypes.AUDIO_FLAC);
    configureCodec("exotest.audio.mpeg", MimeTypes.AUDIO_MPEG);
    configureCodec("exotest.audio.mpegl2", MimeTypes.AUDIO_MPEG_L2);
    configureCodec("exotest.audio.opus", MimeTypes.AUDIO_OPUS);
    configureCodec("exotest.audio.vorbis", MimeTypes.AUDIO_VORBIS);

    // Raw audio should use a bypass mode and never need this codec. However, to easily assert
    // failures of the bypass mode we want to detect when the raw audio is decoded by this class and
    // thus we need a codec to output samples.
    configureCodec("exotest.audio.raw", MimeTypes.AUDIO_RAW);
  }

  @Override
  protected void after() {
    MediaCodecUtil.clearDecoderInfoCache();
  }

  private void configureCodec(String codecName, String mimeType) {
    configureCodec(
        codecName,
        mimeType,
        /* profileLevels= */ ImmutableList.of(),
        /* colorFormats= */ ImmutableList.of());
  }

  private void configureCodec(
      String codecName,
      String mimeType,
      List<MediaCodecInfo.CodecProfileLevel> profileLevels,
      List<Integer> colorFormats) {
    MediaFormat mediaFormat = new MediaFormat();
    mediaFormat.setString(MediaFormat.KEY_MIME, mimeType);
  }

  private static MediaCodecInfo.CodecProfileLevel createProfileLevel(int profile, int level) {
    MediaCodecInfo.CodecProfileLevel profileLevel = new MediaCodecInfo.CodecProfileLevel();
    profileLevel.profile = profile;
    profileLevel.level = level;
    return profileLevel;
  }

  /**
   * A {@link ShadowMediaCodec.CodecConfig.Codec} that passes data through without modifying it.
   *
   * <p>Note: This currently drops all audio data - removing this restriction is tracked in
   * [internal b/174737370].
   */
}
