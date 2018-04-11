package de.stefanmedack.ccctv.ui.detail.uiModels

import info.metadude.kotlin.library.c3media.models.Event

data class VideoPlaybackUiModel(val event: Event, val retainedPlaybackSeconds: Int)